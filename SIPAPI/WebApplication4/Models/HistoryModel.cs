using System;
using System.Collections.Generic;
using System.ComponentModel.DataAnnotations;
using System.Linq;
using System.Web;

namespace WebApplication4.Models
{
    public class HistoryModel
    {
        [Key]
        public int HistoryId { get; set; }
        public string CallerEmail { get; set; }
        public int CallStatus { get; set; }      // 0: Call in,  1: Call out
        public string CallSummary { get; set; }
        public string CallDate { get; set; }
    }
}