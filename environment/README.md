# Guppy Environment driver

A JDBC driver that looks for environment variables to delegate to another JDBC
driver.

E.g. if you set the JDBC url in your application to be jdbc:environment:0 it
will look for environment variables of the format:

```shell
  GUPPY_ENVIRONMENT.0.URL=
  GUPPY_ENVIRONMENT.0.PROPERTY.1=name=value
  GUPPY_ENVIRONMENT.0.PROPERTY.2=name2=value2
```
