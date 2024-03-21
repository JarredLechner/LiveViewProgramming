function setUp() {
  if (window.EventSource) {
    const source = new EventSource("/events");

    source.onmessage = function (event) {
      const splitPos = event.data.indexOf(":");
      const action = event.data.slice(0, splitPos);
      const data = event.data.slice(splitPos + 1).replaceAll("\\n", "\n");

      switch (action) {
        case "script":
          Function(data).apply(); // https://www.educative.io/answers/eval-vs-function-in-javascript
          break;
        case "scriptV2": {
          const newElement = document.createElement("script");
          newElement.innerHTML = data;
          document.body.appendChild(newElement);
          break;
        }
        case "write": {
          const newElement = document.createElement("p");
          newElement.innerHTML = data;
          document.getElementById("events").appendChild(newElement);
          break;
        }
        case "load": {
          const newElement = document.createElement("script");
          newElement.src = data;
          newElement.onload = function (_) {
            fetch("/loaded", {method: "post"}).catch(console.log);
          }
          document.body.appendChild(newElement);
          break;
        }
        default:
          console.log("Unknown Action");
          break;
      }
    };

    source.onerror = function (error) {
      console.error("EventSource failed:", error);
      source.close();
    };
  } else {
    document.getElementById("events").innerHTML =
      "Your browser does not support Server-Sent Events.";
  }
}

const Clerk = {};
setUp();
