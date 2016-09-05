using System;
using System.Collections.Generic;
using System.ComponentModel.DataAnnotations;
using System.Linq;
using System.Web;

namespace WebApplication4.Models
{
    public class UserModel
    {
        [Key]
        public int UserId { get; set; }

        [Required]
        public string UserName { get; set; }
        [Required]
        public string UserEmail { get; set; }
        [Required]
        public string Password { get; set; }

        public string activate_key { get; set; }
        public DateTime activate_until { get; set; }
        public int activate_flag { get; set; }

        public int ConnectionId { get; set; }

        private ICollection<ContactModel> contact_list;
        private ICollection<FavoriteModel> favorite_list;
        private ICollection<HistoryModel> history_list;

        public virtual ICollection<ContactModel> ContactList
        {
            get
            {
                return this.contact_list;
            }
            set
            {
                this.contact_list = value;
            }
        }

        public virtual ICollection<FavoriteModel> FavoriteList
        {
            get
            {
                return this.favorite_list;
            }
            set
            {
                this.favorite_list = value;
            }
        }

        public virtual ICollection<HistoryModel> HistoryList
        {
            get
            {
                return this.history_list;
            }
            set
            {
                this.history_list = value;
            }
        }

        public UserModel()
        {
            this.contact_list = new HashSet<ContactModel>();
            this.favorite_list = new HashSet<FavoriteModel>();
            this.history_list = new HashSet<HistoryModel>();
        }
    }
}