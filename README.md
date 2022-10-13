# Container Auto Tune Project

![license](https://img.shields.io/badge/license-Apache--2.0-green.svg)

Container Auto Tune is an intelligent parameter tuning product that helps developers, operators automatically adjust the application, analyzes JVM reasonable configuration parameters through intelligent algorithms.Please visit the [official site](https://www.opentrs.com/tmaestro/home) for the quick start guide and documentation.

We are now collecting user info to help us to improve further. Kindly support us by providing your usage information to tmaestro@antgroup.com, thanks :)

## Architecture

![Architecture](https://tmaestro-oss.oss-cn-hongkong.aliyuncs.com/AB8BFAD9-49C4-4690-9983-A20EF9A7962E.png)

## Features

* Automatic data collection
* Intelligent tuning parameter analysis
* Tuning full process hosting
* Intelligent risk identification

## Quick Start

```bash
git clone https://github.com/XXXX.git
cd tmaestro-lite
```

### Compile locally
Execute command
```shell
make
```


### run locally

* K8s deploy Container Auto Tune
  * configure 
  ```text
  please set configurations of mongoDB, redis, mysql in tmaestro-lite.yaml file for below item value.
  
  MONGO_DOMAIN,
  MONGO_USERNAME,
  MONGO_PASSWORD,
  MONGO_DATABASE,
  
  REDIS_HOST,
  REDIS_PASSWORD,
  
  MYSQL_URL,
  MYSQL_USERNAME
  ```

  * install Container Auto Tune
  ```shell
  kubectl apply -f tmaestro-lite.yaml
  ```

  * deploy twatch(not necessary)
  ```shell
  kubectl apply -f twatch.yaml
  ```

### install redis(not necessary)
```shell
kubectl apply -f deploy/storage/redis.yaml
```

### install mongodb(not necessary)
```shell
kubectl apply -f deploy/storage/mongodb.yaml
```


## Contact

* Mailing list:
  * dev list: for dev/user discussion. [us](mailto:tmaestro@antgroup.com)

* Bugs: 
* Gitter: 
* Twitter: 

## Contributing

We love contributions! Before taking any further steps, please take a look at [Contributing to Container Auto Tune](./CONTRIBUTING.md).

## Want to Help?

Want to report a bug, contribute some code, or improve documentation? Excellent! Read up on our [template](XX) for contributing and then check out one of our issues labeled as help wanted or good first issue.

## Reporting a security vulnerability

Please report security vulnerabilities to [us](mailto:tmaestro@antgroup.com) privately.

## Acknowledgements
It is very grateful to the people who have contributed codes in the history of Container Auto Tune.

## License

Ant Group Container Auto Tune is distributed under the [Apache License, version 2.0](http://www.apache.org/licenses/LICENSE-2.0.html).
