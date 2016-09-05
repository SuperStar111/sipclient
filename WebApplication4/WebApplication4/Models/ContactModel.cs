using System;
using System.Collections.Generic;
using System.ComponentModel.DataAnnotations;
using System.Linq;
using System.Web;

namespace WebApplication4.Models
{
    public class ContactModel
    {
        [Key]
        public int ContactId { get; set; }
        public string Name { get; set; }
        public string Email { get; set; }
    }
}