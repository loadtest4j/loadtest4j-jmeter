# loadtest4j-jmeter

[![Build Status](https://travis-ci.com/loadtest4j/loadtest4j-jmeter.svg?branch=master)](https://travis-ci.com/loadtest4j/loadtest4j-jmeter)
[![Codecov](https://codecov.io/gh/loadtest4j/loadtest4j-jmeter/branch/master/graph/badge.svg)](https://codecov.io/gh/loadtest4j/loadtest4j-jmeter)

Apache JMeter driver for loadtest4j.

## Setup

1. **Add the library** to your `pom.xml`:

    ```xml
    <dependency>
        <groupId>com.github.loadtest4j</groupId>
        <artifactId>loadtest4j-jmeter</artifactId>
        <version>[version]</version>
        <scope>test</scope>
    </dependency>   
    ```

2. **Configure the driver** in `src/test/resources/loadtest4j.properties`:
    
    ```properties
    loadtest4j.driver.domain = example.com
    loadtest4j.driver.numThreads = 1
    loadtest4j.driver.port = 443
    loadtest4j.driver.protocol = https
    loadtest4j.driver.rampUp = 5
    ```

3. **Write your load tests** using the standard [LoadTester API](https://github.com/loadtest4j/loadtest4j).



