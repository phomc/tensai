# Keybinding API Specification

## Introduction
The purpose of Keybinding API is to provide a stable, secure and convenient way for server-side mods to capture client's key-states.

## Objectives

The keybinding API must meet the following requirements:
- Allows the server to listen to key events from client
- The server has no ability to modify client's key-states
- Key capture must be under player control
- Data transfer must be lightweight, secure and reliable

## Terminology

### 1. Input type
Defines the source of input (input peripheral device). There are two notable types of input:
1. "Keyboard" keys
2. Mouse buttons

### 2. Input code
An input is assigned a number identifier. They are more convenient to work in programming than using their translated names.<br>
For example: The `Enter` key has a key code of `13` <br>
Input code is the same in almost environment: web, Minecraft, other OpenGL applications, etc

### 3. Tensai key
Normally, to identify an input, we need two parameters:
- Input type
- Input code

For convenience, in Tensai, keyboard input and mouse input is merged into the same category called `Key` with the following convention:
- The first `1 << 9` keys are keyboard input
- The rest is mouse input

**Ambiguous:** The term `Key` can be ambiguous: whether it refers to `Keyboard key` or `(Tensai) Key`. Therefore, from now on, we agree that `Key` means `(Tensai) key` referring either keyboard input or mouse input.
<br>

**Note:** Key is not identified by case. For example, key `A` or `a` has the same code `65`. However, two characters `A` and `a` are different:
- `A` has a `char code` of 65, typed by the key combination `Shift + A` (Caps lock off), or `A` (Caps lock on)
- `a` has a `char code` of 97, typed by the `A` key (Caps lock off), or `Shift + A` (Caps lock on)<br>

### 4. Events

There are two main input events:
- Press (Press Down): To hold down a key
- Release (Press Up): No longer hold down a key

A typical user input looks like this: ``Press Press Press Press ... Press Release``. When the user holds down a key, multiple `press` events will be sent. The number of times is non-deterministic. This ends with a single `release` event when the key is released.
- For example, a click is `N mouse press + 1 mouse release`. We mostly do not care about how many `press` are there. A key can be hold down for a long time until it is pressed up.
- The number of `press` is used in typing. If there are 5 `press`, the user is typing 5 identical characters. A `sensitivity` option may exist, so 5 `press` may make up only 3 characters.

### 5. Key-binding
Key-binding is a named button which binds to a specific key. For example, we can define:
```
"Open Inventory" --> Key E (Code = 69)
"Attack" ----------> Left Mouse Button (Code = 1 << 9)
```

Key-binding is editable according to user preference.

```
"Open Inventory" (default: E) -----> ESC
"Close Inventory" (default: ESC) --> E
```

Since a key-binding can bind to a different key than default. It is possible that we may run into a collision (duplication) in which two different key-bindings try to bind to the same key. In Minecraft, the key-binding which has the default key identical to the conflicted key gains prioritized, while the other key-binding is unbounded until the player resolves the issue.

```
"Open Inventory" (default: E) --------> E (prioritized)
"Close Inventory" (default: ESC) -------↑ (collided)
```

## Tensai Key-binding
This section discusses the Keybinding API features in details.

### 1. Capture methods
Currently, Tensai offers two methods of key-capturing:
- Normal: Tensai utilizes the Fabric API and Minecraft system to register a key-binding. The key-binding works exactly what it is in Minecraft, and it can be editable by the player.
- Capture Enforcement: Enforces capturing the state of a specific key when we can not register a key-binding normally due to collision. We use an already-registered key-binding for reference. The key-binding can be variable (since a key can be binded by different key-bindings, but only one of them at once)

### 2. KeyBinding

For convenience, both methods use the same `KeyBinding` structure:
```
Name: The name of the key-binding
Default key: The key to bind by default
Flags: Key-binding flags (see below)
```

<br>**Identification**
- The default key is used to identify keybinding since it is constant
- It is impossible to have two key-binding with the same default key

### 3. Key State

`KeyState` structure is used to store the current state of a key-binding.

```
TimesPressed: How many times the event `pressed` occurred (Non-negative)
Pressed: Whether the key-binding is current pressed
Dirty: A mask value to keep track of which properties were changed
  - Bit 0 for TimesPressed
  - Bit 1 for Pressed
```

#### "wasPressed" and "isPressed"

In the current implementation, there are two methods `isPressed` and `wasPressed`. They are preserved to be the same as in Fabric environment. 
- `isPressed` returns the `pressed` property. It describes whether the key is currently pressed.
- `wasPressed` checks whether `TimesPressed` is positive and decrease it by `1`

So how they work exactly?
- Every tick, Minecraft polls the `press` event using GLFW. A batch may have 1 - 3 `press` or more.
- For example, while the key `A` is pressed, multiple `press` will be counted to `TimesPressed` property. The `pressed` property, however, is a boolean. It is `true` when a `press` event is received, and is `false` when a `release` event happens.
- `isPressed`, as its name stated, shows the current state of the key; while `wasPressed` may contain the count of past `press`.
- `isPressed` is recommended for non-continuous key check, while `wasPressed` is suitable for typing check.

#### Resetting
In Minecraft, the key state is cleaned when a GUI is opened.

#### State Update
- Tensai is responsible to send state updates from client to server. It is a unidirectional synchronization. No state update is sent back to the client.
- State updates are batched in a single message (see details below)

#### Conflict
A conflict can occur if both key-bindings issued by Tensai listening to the same key. Assume we have:
  - A key-binding `#0` with default key `A` managed by Tensai
  - A key-binding `#1` with default key `B` created by whoever

Then, we attempt to register another one `#2` with default key `B`. Since the `B` is already occupied, we have to enable `Capture Enforcement` to listen to the `B` key.<br>
At this point, we have:
```
Key-binding  | Key    | Method    | Reference
#0 -----------> A       Normal
#1 -----------> B       Normal
#2 -------------↑       Enforced      #1
```

Then, we swap the key of the two normal key-binding. Since we have an enforced key-binding listening to `B`, we have to re-update the reference
```
Key-binding  | Key    | Method    | Reference
#0 -----------> B <--   Normal
#1 -----------> A   |   Normal
#2 -----------------|   Enforced      #0
```
At this stage, both `#0` and `#2` managed by Tensai is listening to the same key `B`.<br>
<br>
**We agree that:** If multiple key-bindings managed by Tensai are listening to the same key, we ignore all enforced key-binding.


### 4. Key flags
The key flag controls how a key-binding should behave in
client-side.

1. Capture Enforcement
   - When FLAG_CAPTURE_ENFORCEMENT is enabled, if registration fails due to duplication (KeyBinding.RegisterStatus.KEY_DUPLICATED), Tensai forcibly listens to the key without the need of registering it to the client. In this case, the key will not appear in the settings menu, and thus is not editable.
   - An enforced key-binding registration returns KeyBinding.RegisterStatus.CAPTURE_ENFORCED.
   - The flag is disabled by default.
2. Editable
   - For normal key-bindings which were registered successfully without duplication or the use of FLAG_CAPTURE_ENFORCEMENT, the FLAG_KEY_EDITABLE flag permits binding to a different key.
   - For example: We have a key-binding Key.KEY_Z to "Launch a rocket". If this flag is enabled, players can bind to a different key such as Key.KEY_X to do the same operation.
   - This is the intended feature in Minecraft, thus the flag is enabled by default.
3. Optimized State Update
   - When FLAG_OPTIMIZED_STATE_UPDATE is enabled, key state update is only sent to the server if and only if the pressed property changes. The property can be obtained using KeyState.isPressed().
   - The flag helps to reduce the workload of exchanging information between client and server. However, the trade-off is that the timesPressed property (obtained using KeyState.getTimesPressed()) and its relevant method KeyState.wasPressed() will not work.
   - The flag is recommended for non-continuous key press. A click, for example, requires a key press (up) and a key release while the amount of press times does not matter.
   - The flag is disabled by default.

_(Copied from Javadocs)_
<br>

**Illustration table:**

|         	          |          Normal                   	           |                       Enforced                                         	                        |
|:------------------:|:---------------------------------------------:|:-----------------------------------------------------------------------------------------------:|
| How to register? 	 | Register normally and no collision happened 	 | Flag "Capture Enforcement" required.<br> Attempted normal registration but collision happened 	 |
|   Be named     	   |   ✓<br>(Appears in settings menu)        	    |        ✓<br>(However, has no functionality currently)                                 	         |
| Set default key 	  |            ✓                     	            |                  ✓<br>(Remains permanently)                                  	                  |
|   Edit key     	   |  ✓<br>(Flag "Editable" must be enabled)    	  |                         ✗                                             	                         |
|   Reference    	   |            ✗                     	            |      ✓<br>(Need a dynamic reference to an existing key-binding)                         	       |

### Registration

The workflow of registration as follows:

1. First, in server-side, checks whether the key-binding is already registered.
   - If `true`: returns `Already-registered`
   - If `false`: continue to step 2
2. In client-side, checks if the key-binding is already registered
   - If `true`: continue to step 3
   - If `false`: continue to step 4
3. Checks whether the key-binding is flagged `Capture Enforcement`
   - If `true`: continue to step 4
   - If `false`: returns `Key duplicated`
4. Asks the player for permission
    - Can be bypassed if the permission is already granted
    - If `true`: continues to step 5
    - If `false`: returns `Client rejected`
5. Registers the key-binding
    - Returns `Success` for normal registration
    - Returns `Key enforced` for enforced registration

**List of registration response:**
1. Already-registered
   - "Already-registered" means there was a successful registration made by whatever server-side mod (plugin) using Tensai. It is possible to listen to its state updates. However, the key-binding behaviour and how the state update will return is defined by the very-first mod (plugin).
   - This is the only server-side error
2. Status = 0: Client rejected
   - The keybinding is not conflicted, and might be successfully registered. However, the client rejects the registration of the key.
3. Status = 1: Key duplicated
   - KeyBinding.RegisterStatus.KEY_DUPLICATED means the key-binding is conflicted with another one registered by the Minecraft client or other client-side mods. Key-capturing is unavailable.
4. Status = 2: Key enforced
   - KeyBinding.RegisterStatus.CAPTURE_ENFORCED is the same as KeyBinding.RegisterStatus.KEY_DUPLICATED except that key-capturing is forced to be operable.
5. Status = 3: Success
   - The keybinding is successfully registered in client-side.

## Networking

### Packet identifier (PID)
- 1: Registration Request
- 2: Registration Response
- 3: Key State Update

### (Client-bound) Registration Request
- Registration request can be sent any time during play phase

```
[byte]  PID
[int]   Size of keybinding map
For each keybinding entry:
    [int]   Default key code
    [UTF]   Key-binding name
    [byte]  Key-binding flags
```

### (Server-bound) Registration Response
- The client has full control of whether to accept or reject a keybinding. A prompt is opened to ask the player's decision.
- The client must return the result to server immediately.

```
[byte]   PID
[int]    Size of keybinding map
For each keybinding entry:
    [int]    Default key code
    [byte]   Key registration status
```

### (Server-bound) Key State Update
- The client holds a table of key states. Every tick, if a change in that table is detected, a "key state update" packet is sent. In other words, all key states in an interval are contained in a single packet. The client must send only key states that have been recently updated.

```
[byte]  PID
[int]   Number of updated key states
For each updated keybinding entry:
    [int]     Default key code
    [short]   Pressed times
    [bool]    Is pressed
    [byte]    Dirty mask
```
