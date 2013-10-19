using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace Server
{
    class Message
    {
        public string Sender { get; set; }
        public string Recipient { get; set; }
        public string Note { get; set; }
        public int Message_ID { get; set; }
        public int Time { get; set; }
        public GPS_Coordinate Location { get; set; }
        
        public DateTime GetFormattedTime(long unixTime)
        {
            var epoch = new DateTime(1970, 1, 1, 0, 0, 0, DateTimeKind.Utc);
            return epoch.AddSeconds(unixTime);
        }
    }
}
