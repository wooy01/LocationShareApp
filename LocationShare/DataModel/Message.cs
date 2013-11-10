using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace DataModel
{
    public class Message
    {
        public int ID { get; set; }
        public string SenderUID { get; set; }
        public string RecipientUID { get; set; }
        public string Content { get; set; }
        public Location Location { get; set; }
        public DateTime SendDT { get; set; }
        public DateTime? ReceiveDT { get; set; }
    }
}
