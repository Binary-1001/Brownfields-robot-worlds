# Brownfields_robot_worlds_cjc_08

# 🤖 Robot World

**Robot World** is a client-server system written in Java. It allows clients to control robots that navigate a dynamic world filled with obstacles. The server manages the world state, while clients launch robots and issue movement and control commands.

---

## 🧠 Description

This project includes:

- **Server Program**: Manages the game world, including robot positions, obstacles, and state updates.
- **Client Program**: Connects to the server, launches a robot, and sends commands (e.g., `launch`, `move`, `look`).

---

## 🖧 How It Works

Follows a client/server model:

- **Server**:
    - Listens for incoming connections.
    - Handles multiple robot clients concurrently.
    - Processes robot instructions and updates the shared world.
- **Client**:
    - Connects to the server using sockets.
    - Sends JSON-encoded robot commands.
    - Receives responses about world state and robot feedback.

Robots begin with no knowledge of the world and explore to learn about it.

---

## 🔄 Communication

All communication uses the **Robot Worlds Protocol** — a JSON-based custom protocol that allows:

- Any client to connect to any compatible server on the same network.
- Cross-team server/client interaction (protocol must match).

---

## ✨ Features

- Launch and control multiple robots
- Collision detection with obstacles and other robots
- Multi-client support using Java threads
- Custom protocol using JSON for communication
- Serializable world configuration
- Expandable world logic with modular classes

---

## 📚 Technologies Used

- Java (JDK 11+)
- Maven
- JSON processing libraries (e.g. `org.json`, `Gson`)
- Java Sockets and Threads

---

## 🛠 Installation

### 1. Clone the repository
```bash
git clone https://gitlab.wethinkco.de/odlekalcjc025/brownfields-robot-worlds-cjc-08.git
cd brownfields-robot-worlds-cjc-08
```


## How to play

### Launch Server

```bash
make launch-server
```

### Launch Client

```bash
make launch-client
```

### Launch Server and Client

```bash
make launch-both
```
