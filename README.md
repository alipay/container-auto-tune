# Container Auto Tune Project

![license](https://img.shields.io/badge/license-Apache--2.0-green.svg)

[Container Auto Tune](https://www.opentrs.com/tmaestro/home) is an intelligent parameter tuning product that helps developers, operators automatically adjust the application, analyzes JVM reasonable configuration parameters through intelligent algorithms.Please visit the [official site](https://www.opentrs.com/tmaestro/home) for the quick start guide and documentation.

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
git clone https://github.com/alipay/container-auto-tune.git
cd tmaestro-lite
```

### Compile locally
Execute command
```shell
make
```

## Prerequisites

please confirm the below components installed before you run on your local environment.
* Redis
* MongoDB
* Mysql
* kubectl

please set configurations of mongoDB, redis, mysql in tmaestro-properties-configmap.yaml file for below item value.
  ```text
  MONGO_DOMAIN,
  MONGO_USERNAME,
  MONGO_PASSWORD,
  MONGO_DATABASE,
  
  REDIS_HOST,
  REDIS_PASSWORD,
  
  MYSQL_URL,
  MYSQL_USERNAME
  ```

### import tables of Mysql
* import scripts the following mysql scripts to create database and tables which service stores
```text
  deploy/mysql/tmaestro-lite-database-create.sql
  deploy/mysql/tmaestro-lite-tables-create.sql
```

## run locally
```text
bash start.sh
```

## Contact us

* Mailing list:
    * dev list: for dev/user discussion. [us](mailto:tmaestro@antgroup.com)

* Bugs:
* Gitter:
* Twitter:
* DingTalk   
  ![img.png](img.png)

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