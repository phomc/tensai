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
- A click = N mouse down + 1 mouse up
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
[int]   Size of keybinding map
For each keybinding entry:
    [int]   Key code
    [UTF]   Key name
    [byte]  Key flags
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

### (Server-bound) Key State Update
- The client holds a table of key states. Every tick, if a change in that table is detected, a "key state update" packet is sent. In other words, all key states in an interval are contained in a single packet. The client only sends key states that have been recently updated.

```
[byte]  PID
[int]   Number of updated key states
For each updated keybinding entry:
    [int]     Key code
    [short]   Pressed times
    [bool]    Is pressed
    [byte]    Dirty
```

## Key Flags
(Copied from Javadocs)

### Capture Enforcement
- When FLAG_CAPTURE_ENFORCEMENT is enabled, if registration fails due to duplication (KeyBinding.RegisterStatus.KEY_DUPLICATED), Tensai forcibly listens to the key without the need of registering it to the client. In this case, the key will not appear in the settings menu, and thus is not editable.
- An enforced key-binding registration returns KeyBinding.RegisterStatus.CAPTURE_ENFORCED.
- The flag is disabled by default.

### Editable
- For normal key-bindings which were registered successfully without duplication or the use of FLAG_CAPTURE_ENFORCEMENT, the FLAG_KEY_EDITABLE flag permits binding to a different key. 
- For example: We have a key-binding Key.KEY_Z to "Launch a rocket". If this flag is enabled, players can bind to a different key such as Key.KEY_X to do the same operation. 
- This is the intended feature in Minecraft, thus the flag is enabled by default.

### Optimized State Update
- When FLAG_OPTIMIZED_STATE_UPDATE is enabled, key state update is only sent to the server if and only if the pressed property changes. The property can be obtained using KeyState.isPressed(). 
- The flag helps to reduce the workload of exchanging information between client and server. However, the trade-off is that the timesPressed property (obtained using KeyState.getTimesPressed()) and its relevant method KeyState.wasPressed() will not work. 
- The flag is recommended for non-continuous key press. A click, for example, requires a key press (up) and a key release while the amount of press times does not matter. 
- The flag is disabled by default.

## Key Registration Response

### Already-registered
- "Already-registered" means there was a successful registration made by whatever server-side mod (plugin) using Tensai. It is possible to listen to its state updates. However, the key-binding behaviour and how the state update will return is defined by the very-first mod (plugin).
- This is the only server-side error

### Status 0: Client rejected
The keybinding is not conflicted, and might be successfully registered. However, the client rejects the registration of the key.

### Status 1: Key duplicated
KeyBinding.RegisterStatus.KEY_DUPLICATED means the key-binding is conflicted with another one registered by the Minecraft client or other client-side mods. Key-capturing is unavailable.

### Status 2: Key enforced
KeyBinding.RegisterStatus.CAPTURE_ENFORCED is the same as KeyBinding.RegisterStatus.KEY_DUPLICATED except that key-capturing is forced to be operable.

### Status 3: Success
The keybinding is successfully registered in client-side.

## Implementation

Tensai utilizes Minecraft's key-binding system, so the registration process also follows the system's rules:
- Minecraft, Tensai and other client-side mods can register key-binding at initialization phase
- The key-bind's default key must not be registered before
- A key-binding can be configured in the settings menu. Duplicated ones are shown in red, and the one differ from its default key is not usable.
- Minecraft preserves a few keys. Only available ones can be registered using Fabric API
