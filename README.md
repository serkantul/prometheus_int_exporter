# prometheus_int_exporter
Prometheus exporter for INT

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


## Useful Links
Histogram explanation

http://linuxczar.net/blog/2016/12/31/prometheus-histograms/

Demo Service
https://github.com/juliusv/prometheus_demo_service/blob/master/api.go

Tutorial
https://ordina-jworks.github.io/monitoring/2016/09/23/Monitoring-with-Prometheus.html

Group Left Explanation
https://www.robustperception.io/using-group_left-to-calculate-label-proportions/
