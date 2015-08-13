Complete installation from source
---------------------------------

sbt avro
========

You need the sbt plugin for AVRO:
```
git clone git@github.com:med-at-scale/sbt-avro.git
cd sbt-avro/
sbt publishLocal
cd ..
```

GA4GH java classes
==================

You need the java classes for ga4gh from [https://github.com/med-at-scale/ga4gh-model-java](https://github.com/med-at-scale/ga4gh-model-java)

```
git clone git@github.com:med-at-scale/ga4gh-model-java.git
cd ga4gh-model-java/
```

### GA4GH version 0.5.1

In the ga4gh-model-java project, if working with ga4gh version 0.5.1, you need to checkout the right tag

```
git fetch --tags
git checkout schemas-v0.5.1

git submodule init
git submodule update
```

Then you can install the package in you local repo:
```
sbt publishM2
cd ..
```

### GA4GH version 0.6.0

For the latest version, checkout the master branch:

```
git checkout master
git submodule update
sbt publishM2
cd ..
```

Artifacts for ga4gh java classes are now available in your m2 local repo.

GA4GH Server
============
```
git clone git@github.com:med-at-scale/high-health.git
subl high-health
cd high-health/
```
### Run the server

For ga4gh version 0.5.1 (you must have installed the ga4gh classes v0.5.1 --project ```ga4gh-model-java```):
```
sbt -Dga4gh.version=0.5.1 run
```
The default ga4gh version is 0.6.0:
```
sbt run
```
equivalent to 
```
sbt -Dga4gh.version=0.6.0 run
```

### Make a distribution

You can also package a self contained application not requiring sbt to run:

sbt -Dga4gh.version=0.5.1 dist

or

sbt dist

A zip file containg the application will be created in ```target/universal```.

### Run the distribution

unzip the archive, for example (archive names are generated with some random sequence):

```
unzip high-health-master-36583e9-20150813-235353.zip
cd high-health-master-36583e9-20150813-235353/
bin/high-health
```

You high health server is started. A quick check is to look if the following URL is responding on your browser:

http://localhost:9000/v.0.5.1/beacon/ui

or

http://localhost:9000/v.0.6.0/beacon/ui


Note that it runs on a local spark, and that accessing the S3 repos to query the data works if you set the environment variables with your S3 keys: ```AWS_ACCESS_KEY_ID``` and ```AWS_SECRET_ACCESS_KEY```.

In order to use an existing spark cluster, you need to follow the configuration guide (TODO).



