<h1 style="text-align: center;">GreenField Project</h1>
The GreenField Project is a practical demonstration of the concepts taught during the "Distributed and Pervasive Systems" course for the academic year 2022-2023 at the University of Milan. The primary objective of this project is to design and implement a system to manage a network of robots (simulated through processes). These robots are deployed to collect pollution data, which is then transmitted to a central server for analysis and storage. The entire system is built upon the principles of efficient communication, utilizing gRPC for inter-robot communication and MQTT for transmitting data to the server. Additionally, the server provides a RESTful API to enable clients to access and retrieve statistical information from the collected data, and to let robots register and unregister in the network.

Below are some high-level implementation schemes of the main functionalities of the project.

<h2 style="text-align: center;">Administrator Server</h2>

<img
    src="images\Administrator Server.png"
    width="800" 
    style="display: block; margin: 0 auto"/>

---

<h2 style="text-align: center;">Administrator Client</h2>

<img
    src="images\Administrator Client.png"
    style="display: block; margin: 0 auto"/>

---

<h2 style="text-align: center;">Tasks - Publishing</h2>

<img
    src="images\Tasks - Publishing.png"
    style="display: block; margin: 0 auto"/>

---

<h2 style="text-align: center;">Command execution</h2>

<img
    src="images\Command Execution.png"
    style="display: block; margin: 0 auto"/>

---

<h2 style="text-align: center;">Fault Detection</h2>

<img
    src="images\Fault Detection.png"
    style="display: block; margin: 0 auto"/>

---

<h2 style="text-align: center;">Fault handling</h2>

<img
    src="images\Fault Handling.png"
    style="display: block; margin: 0 auto"/>


The `Access Control Handler` takes all the robots currenlty in the network and:
- If there are no robots, returns.
- Otherwise, it takes all of them (at that specific moment) and adds their IDs to the `Waiting oks` list.

Then, while the `Waiting oks` list is not empty
1. Send message `m = (id, timestamp)` to all the robots in the waiting list.
2. Wait to receive a message from each of them.
3. When the last `ok` has been received, if the waiting list is not empty (other robots have joined the network meanwhile), starts again from step 1.

Then, it takes all the robots in the `Waiting Robots Queue` and send an `ok` message to each of them.


