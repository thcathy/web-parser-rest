# web-parser-rest [![CircleCI](https://circleci.com/gh/thcathy/web-parser-rest.svg?style=svg)](https://circleci.com/gh/thcathy/web-parser-rest)

A server provide restful API to parse resources from websites.

## API to get contents
> We added swagger support to the server. Full list of API can be found from /swagger-ui.html after starting the server.

Type | API | Parameters | Contents
:--- | :--- |:--- |:--- 
Search | GET /rest/search/image/{query} | **query**: string to search | Search Image from seach engine <br>*Google api key may needed*
Finance | GET /rest/quote/indexes | | Real time quote of major indexes in HK and China
Finance | GET /rest/quote/full/{code} | **code**: Hong Kong Stock code e.g. 0005 | Real time quote of HK stocks with PE and NAV
Finance | GET /rest/quote/realtime/list/{codes} | **codes**: comma separated  HK stock codes | Real time quote of HK stocks (faster)
Finance | GET /rest/hkma/report/{yyyymmdd} | **yyyymmdd**: date of report | [HKMA Monetary Base Report](http://www.hkma.gov.hk/eng/market-data-and-statistics/monetary-statistics/)
Finance | GET /rest/index/constituents/{index} | **index**: HSI / HSCEI / HCCI / MSCIChina / MSCIHK | Constituents of major HK Indexes
Finance | GET /rest/index/report/hsinet/{yyyymmdd} | **yyyymmdd**: date of report | [Hang Seng Index Performance Summary ](https://www.hsi.com.hk)
Forum | GET /rest/forum/list/{type}/{batch} | **type**: MUSIC / MOVIE <br> **batch**: int from 1 | Music / Movie from popular Hong Kong Forums<br>*Forum account may needed*

## Configuration
- max connection / per route
- variable to support API

# Starting server
# Command line
# Docker

# Disclaimer

**This is intended for academic purposes. The copyright of contents parsed remain owned by the source websites.
Use it at your own risk.**

# Major Dependency
* [Spring boot](http://projects.spring.io/spring-boot/): application framework
* [Unirest for Java](http://unirest.io/java.html): http client
* [Jsoup](https://jsoup.org/): HTML Parser
* [Springfox](http://springfox.github.io/springfox/): JSON API documentation
