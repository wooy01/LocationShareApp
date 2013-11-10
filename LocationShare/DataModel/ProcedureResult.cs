using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace DataModel
{
    public class ProcedureResult
    {
        public string ResultCode { get; set; }
        public Dictionary<string, object> Results { get; set; }

        public ProcedureResult()
        {
            this.Results = new Dictionary<string, object>();
        }
    }
}
