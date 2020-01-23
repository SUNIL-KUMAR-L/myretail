
# My Retail web application
provides api to 
* get product details with price info for a given product-id
* update price info for a given product-id

##tech stack

* java 11
* spring boot 2.*
* cassandra 3.11.3
* gradle 5.*

##
# Setup
##

install apache-cassandra-3.11.3  under ~/apps  cassandra location : `~/apps/apache-cassandra-3.11.3`

**start cassandra**
```
cd ~/apps/apache-cassandra-3.11.3/bin
./cassandra
```
##
**cassandra shell**
```
cd ~/apps/apache-cassandra-3.11.3/bin
./cqlsh
```
## 
execute below steps
```
CREATE KEYSPACE myretail

  WITH REPLICATION = { 
   
   'class' : 'SimpleStrategy',
    
   'replication_factor' : 1 

};

use myretail;

CREATE TABLE product_price (
    
    product_id text,
    product_price text,
    currency_code text,
    created_datetime timestamp,
    updated_datetime timestamp,
    PRIMARY KEY((product_id))
    
);


desc myretail;
```
_now insert records_
insert price data as per the insert statements at

./others/setup_cassandra_steps.txt

##
**java version**
```

$ javac -version
javac 11.0.5

$ java -version
openjdk version "11.0.5" 2019-10-15
OpenJDK Runtime Environment AdoptOpenJDK (build 11.0.5+10)
OpenJDK 64-Bit Server VM AdoptOpenJDK (build 11.0.5+10, mixed mode)

```
##

## set up myretail application

cd ~/projects/

download or clone the git repo : myretail

cd ~/projects/myretail

./gradlew clean build

===

cd ~/projects/myretail/build/libs

##


run with default application properties

java -jar myretail-0.0.1-SNAPSHOT.jar 

=====
##

or

##
create new **application.properties** with below

```
redskyUrlTemplate=https://redsky.target.com/v2/pdp/tcin/productId?excludes=taxonomy,price,promotion,bulk_ship,rating_and_review_reviews,rating_and_review_statistics,question_answer_statistics

cassandra.contactpoints=localhost

cassandra.port=9042

cassandra.keyspace=myretail

cassandra.consistency=LOCAL_QUORUM

run.MyRetailApplicationRunner=false
```


run below with location of application.properties 

java -jar myretail-0.0.1-SNAPSHOT.jar  --spring.config.location=file:<full_path_of_application_config>/application.properties

##

product available : 13860428, 54643166, 53741618, 53536794 ,76544201

```
{
"product": {
"item": { ...
"tcin": "13860428",
"dpci": "058-34-0436",
"upc": "025192110306",
"product_description": {
"title": "The Big Lebowski (Blu-ray)", ...}
...}

{
"product": {
"item": {
"tcin": "54643166",
"bundle_components": {},
"dpci": "072-10-0656",
"upc": "857561008798",
"product_description": {
"title": "Instant Pot Duo Nova 6 quart 7-in-1 One-Touch Multi-Use Programmable Pressure Cooker with New Easy Seal Lid &#8211; Latest Model",
 ...}
...}

{
"product": {
"esp": {},
"item": {
"tcin": "53741618",
"bundle_components": {},
"dpci": "329-00-0025",
"upc": "885155015426",
"product_description": {
"title": "iRobot Roomba 675 Wi-Fi Connected Robot Vacuum",
...}
...}


{
"product": {
"esp": {},
"item": {
"tcin": "53536794",
"bundle_components": {},
"dpci": "072-08-1669",
"upc": "611247373378",
"product_description": {
"title": "Keurig K-Cafe Special Edition Single-Serve K-Cup Pod Coffee, Latte and Cappuccino Maker - Nickel",
...}
...}


{
"product": {
"item": {
"tcin": "76544201",
"bundle_components": {},
"dpci": "072-04-3056",
"upc": "752356828554",
"product_description": {
"title": "As Seen on TV PowerXL Vortex Air Fryer- 3qt - Black",
...}
...}

```


Product not found : 15117729, 16483589, 16696652, 16752456, 15643793

##

import postman collection

./others/postman/myretail.postman_collection.json

open myretail postman collection 

run each request

or

run with postman runner

##

sample curl request and response

####################################

Fetch product and price data for productId 1386042

####################################
```
request :
curl -X GET http://localhost:8080/myretail/v1/products/13860428

response: 
{"id":"13860428","name":"The Big Lebowski (Blu-ray)","current_price":{"value":"15.99","currency_code":"USD"}}
```
####################################

update product price for productId 13860428

####################################
```
request : 
curl -X PUT \
  http://localhost:8080/myretail/v1/products/13860428 \
  -H 'Content-Type: application/json' \
  -d '{
   "id": "13860428",
    "current_price": {
        "value": "13.99",
        "currency_code": "USD"
    }
}' 

response :
{"id":"13860428","current_price":{"value":"13.99","currency_code":"USD"}}
```
####################################

GET product and price data for productId 13860428 after price update

####################################
```
request :
curl -X GET http://localhost:8080/myretail/v1/products/13860428

response: 
{"id":"13860428","name":"The Big Lebowski (Blu-ray)","current_price":{"value":"13.99","currency_code":"USD"}}
```
####################################



