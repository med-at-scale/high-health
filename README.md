# high-health
## Implementation of GA4GH methods

## Running the server

### S3 keys to access med-at-scale adam formatted 1000genomes genotypes
```
export AWS_ACCESS_KEY_ID=<access-key-id>
export AWS_SECRET_ACCESS_KEY=<secret-access-key>
```
And for the lucky OS X users hitting a ```Unable to load realm info from SCDynamicStore```error:
```
export HADOOP_OPTS="-Djava.security.krb5.realm= -Djava.security.krb5.kdc="
```
(see <a href="https://issues.apache.org/jira/browse/HADOOP-7489">HADOOP-7489</a>)

And by the way, use java 1.7+

### Start the server

```sbt run```


## Architecture
![Architecture](https://raw.github.com/med-at-scale/high-health/master/images/archi.png)
