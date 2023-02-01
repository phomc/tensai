# Keybinding API Specification

## Introduction
The purpose of Keybinding API is to provide a stable, secure and convenient way for server-side mods to capture client's key-states.

## Objectives

The keybinding API must meet the following requirements:
- Allows the server to listen to key events from client
- The server has no ability to modify client's key-states
- Key capture must be under player control
- Data transfer must be secure and reliable

## Terminology

- Key: In Tensai, a key can be either a keyboard key or a mouse button
- Press (Press Down): To hold down a key
    + Mouse Down: a mouse button is pressed
    + Key Down: a key is pressed
- Release (Press Up): No longer hold down a key
    + Mouse Up: a mouse button is released
    + Key Up: a key is released
- A click = Mouse Down + Mouse Up
- GLFW Key code: A number identifier tagged to a key (assigned by GLFW)
    + The code of a keyboard key and a mouse button can be identical
    + The key category is used to distinguish key types
- (Tensai) Key code: A number identifier tagged to a key (assigned by Tensai)
    + The code of a keyboard key and a mouse button is always different
    + Tensai does not use key category
    + The first `1 << 9` elements of the list belong to keyboard keys
    + The rest belongs to mouse buttons
- Key combo (Key combination): Consists of multiple keys

## Networking

### Packet identifier (PID)
- 1: Registration Request
- 2: Registration Response
- 3: Key State Update

### (Client-bound) Registration Request
- After the player joins the server successfully, the server will send a "keybinding registration" request back to the client.<br>
- It is **important** for 3rd plugins to register keybinding at startup (before anyone joins the server)

```
[byte]  PID
[int]   Input delay (ticks)
[int]   Size of keybinding map
For each keybinding entry:
    [int]   Key code
    [UTF]   Key name
```

### (Server-bound) Registration Response
- The client has full control of whether to accept or reject keybinding. A prompt is opened to ask the player's decision.
- After that, the client will return the result to the server.

```
[byte]   PID
[int]    Size of keybinding map
For each keybinding entry:
    [int]    Key code
    [byte]   Key registration status
```

Status:
- 0: client rejected
- 1: key duplicated
- 2: success

### (Server-bound) Key State Update
- The client holds a table of key states. After each delay period, if a change in that table is detected, a "key state update" packet is sent. In other words, all key states in an interval are contained in a single packet. The client only sends key states that have been recently updated.

```
[byte]  PID
[int]   Number of updated key states
For each updated keybinding entry:
    [int]     Key code
    [short]   Pressed times
    [bool]    Is pressed
```
