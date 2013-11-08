
/*
 * Copyright 2013 Karl STEIN
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

import com.karlstein.serializer.CsvSerializer;
import com.karlstein.serializer.JsonSerializer;
import com.karlstein.serializer.XmlSerializer;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.OutputStreamWriter;
import java.util.HashSet;
import java.util.Set;

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
            final BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(System.out));
            final BufferedWriter fileWriter = new BufferedWriter(new FileWriter(new File("object." + format)));

            writer.append("\n" + format + ":\n\n");

            if (format.equals("csv")) {
                // This example serializes a list of objects to a CSV file
                final CsvSerializer csv = new CsvSerializer();

                // Prepare the list
                final Set<ObjectExample> elements = new HashSet<ObjectExample>();
                elements.add(new ObjectExample());
                elements.add(new ObjectExample());
                elements.add(new ObjectExample());

                // Serialize the objects
                csv.write(elements, writer);
                csv.write(elements, fileWriter);
                fileWriter.close();
            }

            if (format.equals("json")) {
                // This example serializes an object to a JSON file
                final JsonSerializer json = new JsonSerializer();

                // Serialize the object
                json.write(new ObjectExample(), writer);
                json.write(new ObjectExample(), fileWriter);
                fileWriter.close();
            }

            if (format.equals("xml")) {
                // This example serializes an object to a XML file
                final XmlSerializer xml = new XmlSerializer();

                // Serialize the object
                xml.write(new ObjectExample(), writer);
                xml.write(new ObjectExample(), fileWriter);
                fileWriter.close();
            }

            // Close the output stream
            writer.close();

        } catch (final Exception e) {
            e.printStackTrace();
        }
    }
}
