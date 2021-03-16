# Guppy Property driver

A JDBC driver that looks for system properties to delegate to another JDBC
driver.

E.g. if you set the JDBC url in your application to be jdbc:property:0 it will 
look for system properties of the format:

```shell
  guppy.property.0.url
  guppy.property.0.property.1=name=value
  guppy.property.0.property.2=name2=value2
```
