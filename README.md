# NtripApp
NTRIP v1 command line client/source

## Usage
`java -jar NtripApp.jar client HOST PORT MOUNT USER[:password] [LAT,LNG]`
to send a NTRIP client request and print the response and data stream from NTRIP caster to stdout.
If LAT,LNG (in decimal degrees) is specified, a NMEA GGA string will be sent to NTRIP caster each second.

`java -jar NtripApp.jar server HOST PORT MOUNT password`
to send a NTRIP source request and stream stdin to NTRIP caster.
