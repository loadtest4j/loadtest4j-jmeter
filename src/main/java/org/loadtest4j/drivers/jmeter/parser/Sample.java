package org.loadtest4j.drivers.jmeter.parser;

/**
 * A Super CSV DTO object for parsing the JMeter output.
 */
public interface Sample {

    void setAllThreads(String allThreads);

    void setBytes(String bytes);

    void setConnect(String connect);

    void setDataType(String dataType);

    String getElapsed();

    void setElapsed(String elapsed);

    void setGrpThreads(String grpThreads);

    void setIdleTime(String idleTime);

    void setLabel(String label);

    void setLatency(String latency);

    void setResponseCode(String responseCode);

    void setResponseMessage(String responseMessage);

    void setSentBytes(String sentBytes);

    String getSuccess();

    void setSuccess(String success);

    void setThreadName(String threadName);

    String getTimeStamp();

    void setTimeStamp(String timeStamp);
}
