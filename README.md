KSerializer (Beta)
=================

**NOTE: Only writing/serializing is supported for now !**

The purpose of this library is to give you a tool to quickly and easily read/write objects to various text formats in Java.
You can do that without having to include numerous jars that are sometimes very heavy.

By default any object you write or read doesn't need to be configured, but sometimes and for optimal results,
you will have to write a few more lines to get the expected result.

Supported text formats :

* CSV (Comma Separated Values)
* JSON (JavaScript Object Notation)
* XML (Extended Markup Language)


Writing
-------

**Writing CSV**

```java
// Create the serializer and the writer
final CsvSerializer csv = new CsvSerializer();
final BufferedWriter writer = new BufferedWriter(new FileWriter(new File("fruits.csv")));

// Fill the list
final Set<Fruit> fruits = new HashSet<Fruit>();
fruits.add(new Fruit("Apple", "Red"));
fruits.add(new Fruit("Banana", "Yellow"));
fruits.add(new Fruit("Kiwi", "Green"));

// Write the columns
csv.writeHeaders(Fruit.class, writer);

// Write the objects
csv.write(fruits, writer);

// Close the writer
writer.close();
```


**Writing JSON**

```java
// Create the serializer and the writer
final JsonSerializer json = new JsonSerializer();
final BufferedWriter writer = new BufferedWriter(new FileWriter(new File("person.json")));

// Serialize the object
json.write(new Person("Douglas", "Crockford"), writer);

// Close the writer
writer.close();
```


**Writing XML**

```java
// Create the serializer and the writer
final XmlSerializer xml = new XmlSerializer();
final BufferedWriter writer = new BufferedWriter(new FileWriter(new File("person.xml")));

// Write the XML header
xml.writeHeader(writer);

// Write the object
xml.write(new Person("Linus", "Torvalds"), writer);

// Close the writer
writer.close();
```


Filtering
---------

It is possible to filter the exported fields using a white list to define only the fields you want
and/or a blacklist to define the fields you don't want. If you mix includes and excludes filters for the same class,
only includes will be used.

```java
// Create the serializer and the writer
final JsonSerializer json = new JsonSerializer();
final BufferedWriter writer = new BufferedWriter(new FileWriter(new File("batman.json")));

Hero batman = new Hero();
batman.setName("The Batman");

Enemy jocker = new Enemy();
jocker.setName("The Jocker");
jocker.getEnemies().add(batman);

batman.getEnemies().add(jocker);
batman.getWeapons().add(new Weapon("Batarang"));

// Only export these fields
json.includeField("name", Hero.class);
json.includeField("enemies", Hero.class);

// Ignore the enemies of the enemies to avoid the StackOverflowException (infinite loop)
json.excludeField("enemies", Enemy.class);

// Serialize the object
json.write(batman, writer);

// Close the writer
writer.close();
```

```java
// Create the serializer and the writer
final JsonSerializer json = new JsonSerializer();
final BufferedWriter writer = new BufferedWriter(new FileWriter(new File("person.json")));


// Ignore the friends list
json.excludeField("friends", Person.class);

// Serialize the object
json.write(new Person("Douglas", "Crockford"), writer);

// Close the writer
writer.close();
```

