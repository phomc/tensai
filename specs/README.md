# Tensai Specification

## Terminology
- Server-bound: Packets sent from client to server
- Client-bound: Packets sent from server to client
- UTF: Unicode Transformation Format

## Packet channel
Tensai uses the official plugin channel support as specified here: https://wiki.vg/Plugin_channels <br>

*List of registered channels*
- tensai:keybinding
- tensai:vfx

## Packet data types

```
byte/bool: 1 byte
short/char: 2 bytes
int: 4 bytes
long: 8 bytes
float: 4 bytes
double: 8 bytes
UTF: 2 bytes + up to 65535 bytes

(numbers are signed)
```

## Keybinding

Read here: [Keybinding API.md](Keybinding API.md)
