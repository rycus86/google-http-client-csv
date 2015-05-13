# google-http-client-csv
ObjectParser implementation for Google HTTP Client library to parse objects from CSV files.

### Usage:
```java
final HttpRequest request = ...;
request.setParser(new CsvParser());

final HttpResponse response = request.execute();
final Item[] items = response.parseAs(Item[].class);
```

### Maven setup:
```pom.xml
<dependencies>
  ...
  <dependency>
    <groupId>hu.rycus</groupId>
    <artifactId>google-http-client-csv</artifactId>
    <version>1.0.1</version>
  </dependency>
  ...
<dependencies>
```
