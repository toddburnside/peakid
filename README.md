# peakid

An educational project for myself. This is currently a simple web service built using http4s for the http library, doobie for accessing a PostgreSQL database, and circe for JSON parsing.

Data for "summit" features was obtained from the USGS Geographic Names Information System (GNIS). The goal is to determine which summits are visible from any given location (including elevation). The next step will be to download digital elevation information for each summit from the Google Maps Elevation Service to be able to create a 3D model of each summit. This will allow visualation of summit profiles to aid in identification. The plan is to create a web interface for this using Scala.JS, with D3 used for the summit visualization. A final step would be a mobile app, with the ability to cache the data for a given area and display potentially visible summits depending on which direction the device is pointed. 
