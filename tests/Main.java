/*
 * Copyright 2014 Karl STEIN
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import com.karlstein.tools.serializer.CsvSerializer;
import com.karlstein.tools.serializer.JsonSerializer;
import com.karlstein.tools.serializer.XmlSerializer;

import java.io.*;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class Main {

    /**
     * Starts the tests
     *
     * @param args the arguments
     */
    public static void main(final String[] args) {
        try {
            // Define the output format
            final String format = "xml";
            final File file = new File("object." + format);
            final BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(System.out));
            final BufferedWriter fileWriter = new BufferedWriter(new FileWriter(file));
            final BufferedReader fileReader = new BufferedReader(new FileReader(file));

            writer.append("\n" + format + ":\n\n");

            if (format.equals("csv")) {
                // This example serializes a list of objects to a CSV file
                final CsvSerializer csv = new CsvSerializer();

                // Prepare the list
                final List<ObjectExample> elements = new ArrayList<ObjectExample>();
                elements.add(new ObjectExample());
                elements.add(new ObjectExample());
                elements.add(new ObjectExample());
                elements.get(0)._oString = "CUSTOM VALUE";

                // Write the objects
                csv.write(elements, writer);
                csv.write(elements, fileWriter);
                fileWriter.close();

                // Read the objects
                final Collection<ObjectExample> input = csv.read(fileReader, ObjectExample.class);
                System.out.println("Read: " + input);
            }

            if (format.equals("json")) {
                // This example serializes an object to a JSON file
                final JsonSerializer json = new JsonSerializer();

                ObjectExample objA = new ObjectExample();
                ObjectExample objB = new ObjectExample();
                objA._oRecursive = objB;
                objA._oString = "A";
                objA._oRecursiveList.add(objB);
                objB._oRecursive = objA;
                objB._oRecursiveList.add(objA);
                objB._oString = "B";

                json.includeField("_oRecursive", ObjectExample.class);
                json.includeField("_oRecursiveList", ObjectExample.class);
                json.includeField("_oString", ObjectExample.class);

                // Serialize the object
                json.write(objA, writer);
                json.write(objA, fileWriter);
                fileWriter.close();
            }

            if (format.equals("xml")) {
                // This example serializes an object to a XML file
                final XmlSerializer xml = new XmlSerializer();

                ObjectExample objA = new ObjectExample();
                ObjectExample objB = new ObjectExample();
                objA._oRecursive = objB;
                objA._oString = "A";
                objA._oRecursiveList.add(objB);
                objB._oRecursive = objA;
                objB._oRecursiveList.add(objA);
                objB._oString = "B";

                // Serialize the object
                xml.write(objA, writer);
                xml.write(objA, fileWriter);
                fileWriter.close();
            }

            // Close the output stream
            writer.close();

        } catch (final Exception e) {
            e.printStackTrace();
        }
    }
}
