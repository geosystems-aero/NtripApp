# NtripApp
NTRIP v1 command line client/source

## Building

Not yet configured. 

Depends on [geosystems-aero/nmea](https://github.com/geosystems-aero/nmea), [geosystems-aero/JNtrip](https://github.com/geosystems-aero/JNtrip), and [geosystems-aero/GNSS](https://github.com/geosystems-aero/GNSS).

## Usage

`java -jar NtripApp.jar client HOST PORT MOUNT USER[:password] [LAT,LNG]`

to send a NTRIP client request and print the response and data stream from NTRIP caster to stdout.
If LAT,LNG (in decimal degrees) is specified, a NMEA GGA string will be sent to NTRIP caster each second.

`java -jar NtripApp.jar server HOST PORT MOUNT password`

to send a NTRIP source request and stream stdin to NTRIP caster.
