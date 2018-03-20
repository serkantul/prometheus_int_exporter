# PROMETHEUS INT(INBAND NETWORK TELEMETRY) EXPORTER

This repository is made available to support collecting and parsing Telemetry Reports which is specified by P4 Applications Working Group. Metrics related to Inband Network Telemetry are pushed to Prometheus Monitoring agent.
This work is still in progress.

## Steps to install Prometheus

* Create a file in /tmp/prometheus.yaml and copy below:

      global:
      scrape_interval:     15s
      evaluation_interval: 15s

      rule_files:
      # - "first.rules"
      # - "second.rules"

      scrape_configs:
        - job_name: 'prometheus'
          static_configs:
            - targets: ['localhost:9090']

        - job_name: 'int demo'
        # metrics_path defaults to '/metrics'
        # scheme defaults to 'http'.

        scrape_interval: 5s
        static_configs:
          - targets: ['YOUR_IP_ADDRESS:1234']

* Run prometheus with the following command

        sudo docker run -p 9090:9090 -v /tmp/prometheus.yml:/etc/prometheus/prometheus.yml prom/prometheus

* You should able to see

      http://localhost:9090/metrics


## Running exporter

* git clone https://github.com/serkantul/prometheus_int_exporter

* Compile it.  
  mvn compile or mvn verify

* Run the exporter and you should be able to see http://localhost:1234/

      mvn exec:java -Dexec.classpathScope=test -Dexec.mainClass="io.prometheus.client.exporter.ExampleExporter"

* NOTE: To install the jnetpcap

      mvn install:install-file -Dfile="jnetpcap.jar" -DgroupId="jnetpcap" -DartifactId="jnetpcap" -Dversion="1.3.0" -Dpackaging="jar"



* Open Prometheus page http://localhost:9090/graph
* You should able to see our int demo service is up http://localhost:9090/targets
* You can play around with the metrics by using the graph tab in
Prometheus web page. Start typing latency and you'll see available INT metrics.

## Query examples
s1_flow_latency

s2_hop_latency_ms{flows="10.0.10.1:1234->10.0.20.1:8080"}

total_flow_latency_ms{flowspath=~"^10.0.10.1:.*_s3_.*"}

topk(3, total_flow_latency_ms)

## Grafana Integration
    sudo docker run -d --name=grafana -p 3000:3000 grafana/grafana
http://localhost:3000/login

Username/Password is admin/admin

Add data source from Grafana screen:

    Name=Something you like
    URL=http://localhost:9090
    Access=Direct

Add a new dashboard

Add graph

Click the panel title and edit

Choose data source as Mixed

Add your PromQL query like
    s1_flow_latency

## PushGateway Integration

  You need to add the Pushgateway as a target to scrape in configuration file. For example, add these lines to /tmp/prometheus.yml

    - job_name: 'prometheus'
            # metrics_path defaults to '/metrics'
            # scheme defaults to 'http'.

            honor_labels: true
            scrape_interval: 5s
            static_configs:
              - targets: ['YOUR_IP_ADDRESS:9091']

    Note: you should always set honor_labels: true in the scrape config

### Installing and running prom/pushgateway

#### Docker installation:

    docker pull prom/pushgateway
    docker run -d -p 9091:9091 prom/pushgateway

  Although using docker seems to be easiest way, you can use binary releases to install and run pushgateway.

  Download binary releases for your platform from the [release page](https://github.com/prometheus/pushgateway/releases) and unpack the tarball.
    If you want to compile yourself from the sources, you need a working Go setup. Then use the provided Makefile (type make).
    For the most basic setup, just start the binary. To change the address to listen on, use the -web.listen-address flag.
    By default, Pushgateway does not persist metrics. However, the -persistence.file flag allows you to specify a file in which
    the pushed metrics will be persisted (so that they survive restarts of the Pushgateway).

### Using pushgateway

Push a single sample into the group identified by {job="IntDemo_job"}:

    echo "IntDemo_metric 3.14" | curl --data-binary @-
    localhost:9091/metrics/job/IntDemo_job

You can give the specific client address like http://pushgateway.example.org if you have one.
Since no type information has been provided, IntDemo_metric will be of type untyped.

Push something more complex into the group identified by {job="IntDemo_job",instance="some_instance"}:

    cat <<EOF | curl --data-binary @- localhost:9091/metrics/job/IntDemo_job/instance/some_instance
        # TYPE some_metric counter
        some_metric{label="sw01"} 42
        # TYPE another_metric gauge
        # HELP another_metric Just an example.
        another_metric 2398.283
      EOF

Note: how type information and help strings are provided. Those linesare optional, but strongly encouraged for anything more complex.

You can check status of the pushed metrics from

    http://localhost:9091/metrics

Delete all metrics grouped by job and instance:

    curl -X DELETE localhost:9091/metrics/job/IntDemo_job/instance/some_instance
Delete all metrics grouped by job only:

    curl -X DELETE localhost:9091/metrics/job/IntDemo_job

## Useful Links
Histogram explanation

http://linuxczar.net/blog/2016/12/31/prometheus-histograms/

Demo Service
https://github.com/juliusv/prometheus_demo_service/blob/master/api.go

Tutorial
https://ordina-jworks.github.io/monitoring/2016/09/23/Monitoring-with-Prometheus.html

Group Left Explanation
https://www.robustperception.io/using-group_left-to-calculate-label-proportions/
