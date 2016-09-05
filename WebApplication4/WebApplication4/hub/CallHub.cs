using System;
using System.Collections.Generic;
using System.Linq;
using System.Web;
using Microsoft.AspNet.SignalR;

namespace WebApplication4.hub
{
    public class CallHub : Hub
    {
        public void Hello()
        {
            Clients.All.hello();
        }
        public void Send(string name, string message)
        {
            // Call the addNewMessageToPage method to update clients.
            Clients.All.addNewMessageToPage(name, message);
        }

        public void Call(string from, string to)
        {
            // Call the addNewMessageToPage method to update clients.
            Clients.Caller.sendRoom("1001", from, to);
            Clients.Others.sendRoom("1002", from, to);
        }

        public void Call_End(string from, string to)
        {
            Clients.Others.endCall(from, to);
        }

        public void Call_Start(string from, string to)
        {
            Clients.All.startCall(from, to);
        }
    }
}