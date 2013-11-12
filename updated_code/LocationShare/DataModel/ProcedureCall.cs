using Newtonsoft.Json.Linq;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Net.Sockets;
using System.Text;
using System.Threading.Tasks;

namespace DataModel
{
    public class ProcedureCall
    {
        public string Command { get; set; }
        public Dictionary<string, string> Parameters { get; set; }
        public Socket ClientSocket { get; set; }

        public ProcedureCall()
        {
            this.Parameters = new Dictionary<string, string>();
        }
    }
}
