# CiVers Prototype
 
A system designed to take snapshots of websites and
generate [DOI](https://en.wikipedia.org/wiki/Digital_object_identifier)s,
aimed at providing permanently citable web resources, which are otherwise
prone to link-rot.
 
See [here](./docs/README.md) for a description of the system.

## Prerequisites 

- `Docker`
- `docker-compose`

Under Mac and Windows this means just installing `Docker Desktop`, which includes both.

When running under Windows it is also recommended to make use of `GitBash`.

Setup tested under **Ubuntu** Linux, **Mac**, **Windows** (Docker Desktop with WSL-2). 
The user interfaces are tested with `Chromium`, `Chrome` and `Firefox`.

## Getting started

    $ docker-compose up

This starts multiple services, three of which have addresses 
one can visit in the browser:

- http://localhost:8020 # The DOI Registrar
- http://localhost:8021 # The Citator
- http://localhost:8022 # The Widget Host

Please open each of them in its own tab. To learn about the intended behaviour of the system, 
the two primary use cases are documented [here](./docs/README.md#use-cases).

Note that the generated artifacts, screenshots and html files of archived sites, can be found in the `archive` folder
in the root directory of this project.

Note also that apart from the three user interfaces mentioned there exist multiple backend service **APIs**.
The *DOI Registrar* has an API, the *Citator* has one API to request archival of arbitrary websites and one API
which provides the *Widget*. Finally the **Scraper** itself provides an API which makes the scraping code directly accessible
(omitting DOI generation).

For an architectural overview consult the [technical documentation](./docs/README_TECHNICAL.md).

### Notes and Troubleshooting

### Websockets

Of the two sites at `http://localhost:8020/` and `http://localhost:8021` make sure to only keep one tab open for each of them.
This is because only the last opened tab will keep a websocket connection, which is used for automatic updates when resources change.
However, for the `8021` service this does not apply to subsites like `http://localhost:8021/<somePath>`. Here it does not matter how many tabs one opens.

### Red bar on the bottom of the screen

If you encounter in either *Citator* or *DOI Registrar* a red message bar at the bottom of the screen which 
informs about `shadow-cljs - Stale Output!` or `shadow-cljs - Reconnecting ...`, wait a few seconds and refrsh the page. Also wait a few seconds and refresh
if *Widget Host* does not show the widget yet. Make sure everything is fine before you proceed.

### Clean up

To start the test system from scratch again, 
one simply removes some files and folders.

#### Mac and Linux

Execute the following script:

    $ ./clean.sh

#### Windows

Shut down `docker-compose` (if it runs) and
delete all files under `archive`, except `.keep`. 

Delete the directories `citator-data`
and `doi-registrar-data`.

TODO verify if you need some special permissions here.

## Development notes

The *Citator* UI (port `8020`) and the *DOI Registrar* (port `8021`) UI
provide hot code reload via `shadow-cljs`. 

Also, hot code reload is provided for the backend code. The reload happens
on each http request against one of the routes configured in `defroutes`.

### Python development

The **webscraping** code is written in Python. 
The code can be developed outside the docker container, in the local environment.

Apart from `python3` and `pip3`, you will need to install `selenium`:

```bash
$ pip3 install selenium==3.8.0
```

To scrape a website run this script from the root directory of the project:

```bash
civers-prototype$ python3 scraper/scrape.py <some-url> <target-name>
```

The target name will be used to name the generated artifacts in the `archive` folder.

### Working with the REPL

- Uncomment one and comment the other entrypoint in docker-compose.yml, for a given service
- `docker-compose up`
- Connect to the given REPL port from from you editor 

then do

```clojure
clj:user:> (start)
{:started ["#'resources/resources" "#'server/http-server"]}
```
