/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package airbnb_13;

import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.NullWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.Reducer;
import org.apache.hadoop.mapreduce.lib.input.MultipleInputs;
import org.apache.hadoop.mapreduce.lib.input.TextInputFormat;
import org.apache.hadoop.mapreduce.lib.output.TextOutputFormat;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

/**
 *
 * @author khushbuprkh
 */
public class AirBnB_13 {

    public static class ListingMapper extends Mapper<Object, Text, Text, Text> {

        private Text outputKey = new Text();
        private Text outputValue = new Text();

        @Override
        protected void map(Object key, Text value, Context context) throws IOException, InterruptedException {

            StringTokenizer itr = new StringTokenizer(value.toString(), ",");
            while (itr.hasMoreTokens()) {
                int i = 0;
                while (itr.hasMoreTokens()) {
                    String str = itr.nextToken();
                    if (i == 0) {
                        if (!str.equals("id")) {
                            outputKey.set(str);
                        }
                    }
                    i++;
                }
            }
            outputValue.set("L" + value.toString());
            context.write(outputKey, outputValue);
        }

    }

    public static class ReviewMapper extends Mapper<Object, Text, Text, Text> {

        private Text outputKey = new Text();
        private Text outputValue = new Text();

        @Override
        protected void map(Object key, Text value, Context context) throws IOException, InterruptedException {

            StringTokenizer itr = new StringTokenizer(value.toString(), ",");
            while (itr.hasMoreTokens()) {
                int i = 0;
                while (itr.hasMoreTokens()) {
                    String str = itr.nextToken();
                    if (i == 0) {
                        if (!str.equals("listingId")) {
                            outputKey.set(str);
                        }
                    }
                    i++;
                }

            }

            outputValue.set("R" + value.toString());
            context.write(outputKey, outputValue);
        }

    }

    public static class ReducerClass extends Reducer<Text, Text, Text, NullWritable> {

        private ArrayList<String> review = new ArrayList<String>();
        private DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        private String listing = null;

        @Override
        protected void reduce(Text key, Iterable<Text> values, Context context) throws IOException, InterruptedException {
            listing = null;
            review.clear();

            for (Text val : values) {
                if (val.charAt(0) == 'L') {
                    listing = val.toString().substring(1, val.toString().length()).trim();
                } else {
                    review.add(val.toString().substring(1, val.toString().length()).trim());
                }
            }

            if (listing != null) {
                try {
                    String movieTitleWithTags = nestElements(listing, review);

                    context.write(new Text(movieTitleWithTags), NullWritable.get());
                } catch (TransformerException ex) {
                    Logger.getLogger(AirBnB_13.class.getName()).log(Level.SEVERE, null, ex);
                } catch (ParserConfigurationException ex) {
                    Logger.getLogger(AirBnB_13.class.getName()).log(Level.SEVERE, null, ex);
                } catch (SAXException ex) {
                    Logger.getLogger(AirBnB_13.class.getName()).log(Level.SEVERE, null, ex);
                }
            }

        }

        private String nestElements(String listing, List<String> reviews) throws TransformerException, ParserConfigurationException, SAXException, IOException {
            // Create the new document to build the XML
            DocumentBuilder bldr = dbf.newDocumentBuilder();
            Document doc = bldr.newDocument();
            // Copy parent node to document
            String listingEl = null;
            StringTokenizer itr = new StringTokenizer(listing.toString(), ",");
                while (itr.hasMoreTokens()) {
                    int i = 0;
                    while (itr.hasMoreTokens()) {
                        String str = itr.nextToken();
                        if (i == 0) {
                            if (!str.equals("id")) {
                                listingEl=str;
                            }
                        }
                        i++;
                    }

                }
            
            Element toAddlistingEl = doc.createElement("Listing");

            // Copy the attributes of the original post element to the new one
            copyAttributesToElement("Listing", listingEl, toAddlistingEl);

            // For each comment, copy it to the "post" node
            for (String tagsXml : reviews) {

                String reviewsEl = null;
                StringTokenizer iitr = new StringTokenizer(tagsXml.toString(), ",");
                while (iitr.hasMoreTokens()) {
                    int i = 0;
                    while (iitr.hasMoreTokens()) {
                        String str = iitr.nextToken();
                        if (i == 5) {
                            if (!str.equals("comments")) {
                                reviewsEl=str;
                            }
                        }
                        i++;
                    }

                }

                Element toAddreviewsEl = doc.createElement("reviews");

                // Copy the attributes of the original comment element to
                // the new one
                copyAttributesToElement("reviews", reviewsEl, toAddreviewsEl);

                // Add the copied comment to the post element
                toAddlistingEl.appendChild(toAddreviewsEl);
            }

            // Add the post element to the document
            doc.appendChild(toAddlistingEl);

            // Transform the document into a String of XML and return
            return transformDocumentToString(doc);
        }

        private Element getXmlElementFromString(String xml) throws ParserConfigurationException, SAXException, IOException {
            // Create a new document builder
            DocumentBuilder bldr = dbf.newDocumentBuilder();

            return bldr.parse(new InputSource(new StringReader(xml)))
                    .getDocumentElement();
        }

        private void copyAttributesToElement(String key, String value,
                Element element) {

            // For each attribute, copy it to the element
            element.setAttribute(key, value);
        }

        private String transformDocumentToString(Document doc) throws TransformerConfigurationException, TransformerException {

            TransformerFactory tf = TransformerFactory.newInstance();
            Transformer transformer = tf.newTransformer();
            transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION,
                    "yes");
            StringWriter writer = new StringWriter();
            transformer.transform(new DOMSource(doc), new StreamResult(
                    writer));
            // Replace all new line characters with an empty string to have
            // one record per line.
            return writer.getBuffer().toString().replaceAll("\n|\r", "");
        }

    }

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) throws IOException, InterruptedException, ClassNotFoundException {
        // TODO code application logic here
        Configuration conf = new Configuration();
        Job job = Job.getInstance(conf, "PostCommentHierarchy");
        job.setJarByClass(AirBnB_13.class);

        MultipleInputs.addInputPath(job, new Path(args[0]),
                TextInputFormat.class, ListingMapper.class);

        MultipleInputs.addInputPath(job, new Path(args[1]),
                TextInputFormat.class, ReviewMapper.class);

        job.setReducerClass(ReducerClass.class);

        job.setOutputFormatClass(TextOutputFormat.class);
        TextOutputFormat.setOutputPath(job, new Path(args[2]));

        job.setOutputKeyClass(Text.class);
        job.setOutputValueClass(Text.class);

        System.exit(job.waitForCompletion(true) ? 0 : 2);

    }

}
