# loadtest4j-jmeter

[![Build Status](https://travis-ci.com/loadtest4j/loadtest4j-jmeter.svg?branch=master)](https://travis-ci.com/loadtest4j/loadtest4j-jmeter)
[![Codecov](https://codecov.io/gh/loadtest4j/loadtest4j-jmeter/branch/master/graph/badge.svg)](https://codecov.io/gh/loadtest4j/loadtest4j-jmeter)
[![Maven Central](https://img.shields.io/maven-central/v/org.loadtest4j.drivers/loadtest4j-jmeter.svg)](https://repo1.maven.org/maven2/org/loadtest4j/drivers/loadtest4j-jmeter/)

Apache JMeter driver for [loadtest4j](https://github.com/loadtest4j/loadtest4j).

## Setup

### 1. Add the library

Add the library to your Maven project POM:

```xml
<dependency>
    <groupId>org.loadtest4j.drivers</groupId>
    <artifactId>loadtest4j-jmeter</artifactId>
    <scope>test</scope>
</dependency>
```

### 2. Create the load tester

Use **either** the Factory **or** the Builder.

#### Factory

```java
LoadTester loadTester = LoadTesterFactory.getLoadTester();
```

```properties
# src/test/resources/loadtest4j.properties

loadtest4j.driver.domain = example.com
loadtest4j.driver.numThreads = 1
loadtest4j.driver.port = 443
loadtest4j.driver.protocol = https
loadtest4j.driver.rampUp = 5
```

#### Builder

```java
LoadTester loadTester = JMeterBuilder.withUrl("https", "example.com", 443)
                                     .withNumThreads(1)
                                     .withRampUp(5)
                                     .build();
``` 

### 3. **Write load tests** 

Use the [LoadTester API](https://github.com/loadtest4j/loadtest4j) to write load tests:

```java
public class PetStoreLT {

    private static final LoadTester loadTester = /* see Step 2 */ ;

    @Test
    public void shouldFindPets() {
        List<Request> requests = List.of(Request.get("/pet/findByStatus")
                                                .withHeader("Accept", "application/json")
                                                .withQueryParam("status", "available"));

        Result result = loadTester.run(requests);

        assertThat(result.getResponseTime().getPercentile(90))
            .isLessThanOrEqualTo(Duration.ofMillis(500));
    }
}
```

## Generate HTML reports

The driver instructs JMeter to write its JTL report file to `./results/loadtest4j-[timestamp]/result.jtl`.

A standalone copy of JMeter can generate an HTML report from this file with the following command:

```bash
jmeter -g /path/to/result.jtl -o /path/to/html
```

You can also post-process the JTL file with any other compatible tool of your choice.
