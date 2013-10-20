using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace LocationShareServer
{
    public class Message
    {
        public int MessageID { get; set; }
        public string FromName { get; set; }
        public string ToName { get; set; }
        public string Contents { get; set; }
        public float Longitude { get; set; }
        public float Latitude { get; set; }
        public Boolean IsNew { get; set; }
        public DateTime TimeStamp { get; set; }
    }
}
