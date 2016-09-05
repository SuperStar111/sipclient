using System;
using System.Collections.Generic;
using System.Data;
using System.Data.Entity;
using System.Data.Entity.Infrastructure;
using System.Linq;
using System.Net;
using System.Net.Http;
using System.Text;
using System.Threading.Tasks;
using System.Web.Http;
using System.Web.Http.Description;
using WebApplication4.Models;

namespace WebApplication4.Controllers
{
    public class UsersController : ApiController
    {
        private DBContext db = new DBContext();

        // GET: api/Users
        public IQueryable<UserModel> GetUserModels()
        {
            return db.UserModels;
        }

        // GET: api/Users/5
        [ResponseType(typeof(UserModel))]
        public async Task<IHttpActionResult> GetUserModel(int id)
        {
            UserModel userModel = await db.UserModels.FindAsync(id);
            if (userModel == null)
            {
                return NotFound();
            }

            return Ok(userModel);
        }

        // PUT: api/Users/5
        [ResponseType(typeof(void))]
        public async Task<IHttpActionResult> PutUserModel(int id, UserModel userModel)
        {
            if (!ModelState.IsValid)
            {
                return BadRequest(ModelState);
            }

            if (id != userModel.UserId)
            {
                return BadRequest();
            }

            db.Entry(userModel).State = EntityState.Modified;

            try
            {
                await db.SaveChangesAsync();
            }
            catch (DbUpdateConcurrencyException)
            {
                if (!UserModelExists(id))
                {
                    return NotFound();
                }
                else
                {
                    throw;
                }
            }

            return StatusCode(HttpStatusCode.NoContent);
        }

        // POST: api/Users
        [ResponseType(typeof(UserModel))]
        public async Task<IHttpActionResult> PostUserModel(UserModel userModel)
        {
            if (!ModelState.IsValid)
            {
                return BadRequest(ModelState);
            }

            db.UserModels.Add(userModel);
            await db.SaveChangesAsync();

            return CreatedAtRoute("DefaultApi", new { id = userModel.UserId }, userModel);
        }

        // DELETE: api/Users/5
        [ResponseType(typeof(UserModel))]
        public async Task<IHttpActionResult> DeleteUserModel(int id)
        {
            UserModel userModel = await db.UserModels.FindAsync(id);
            if (userModel == null)
            {
                return NotFound();
            }

            db.UserModels.Remove(userModel);
            await db.SaveChangesAsync();

            return Ok(userModel);
        }

        // GET: api/Users/5
        [ResponseType(typeof(UserModel))]
        [HttpPost]
        [Route("api/users/search/{email?}")]
        public async Task<IHttpActionResult> search(string email)
        {
            IEnumerable<UserModel> userlist = db.UserModels.Where(u => u.UserEmail.StartsWith(email));

            List<ContactModel> contactlist = new List<ContactModel>();
            foreach(UserModel user in userlist)
            {
                ContactModel contact = new ContactModel();
                contact.Email = user.UserEmail;
                contact.Name = user.UserName;
                contactlist.Add(contact);
            }
            Dictionary<string, List<ContactModel>> dictionary = new Dictionary<string, List<ContactModel>>();
            dictionary.Add("users", contactlist);
            
            return Ok(dictionary);
        }

        // Signin: api/Users/signin
        [ResponseType(typeof(UserModel))]
        [HttpPost]
        [Route("api/users/signin")]
        public IHttpActionResult signin(string email, string password)
        {
            UserModel user = db.UserModels.FirstOrDefault(u => u.UserEmail .Equals(email));
            if (user == null)
            {
                return BadRequest("This Email does not exist!");
            }

            if (user.Password == md5(password))
            {
                return Ok(user);
            }
            else
            {
                return BadRequest("Password is incorrect!");
            }
            return NotFound();
        }

        // Signup: api/Users/signup/
        [ResponseType(typeof(UserModel))]
        [HttpPost]
        [Route("api/users/signup")]
        public IHttpActionResult signup(UserModel user)
        {

            if (!ModelState.IsValid)
            {
                return BadRequest(ModelState);
            }
            UserModel other = db.UserModels.FirstOrDefault(u => u.UserEmail == user.UserEmail);
            if (other != null)
            {
                return BadRequest("User Already Exist");
            }
            user.Password = md5(user.Password);

            user.activate_flag = 0;  // Unactivated

            Random rnd = new Random();
            int rand = rnd.Next(100000, 999999);
            user.activate_key = Convert.ToString(rand);

            user.activate_until = DateTime.Now.AddMinutes(10);


            db.UserModels.Add(user);
            db.SaveChanges();

            return Ok(user);
        }


        // Activate User: api/Users/activate_user/
        [ResponseType(typeof(UserModel))]
        [HttpPost]
        [Route("api/users/activate_user")]
        public IHttpActionResult activate_user(string email, string activate_key)
        {
            UserModel user = db.UserModels.FirstOrDefault(u => u.UserEmail == email);
            if (user == null)
            {
                return BadRequest("User doesnot Exist!");
            }

            if (DateTime.Now > user.activate_until)
            {
                return BadRequest("Activate Key Expired!");
            }
            if (user.activate_key != null && user.activate_key == activate_key)
            {
                user.activate_flag = 1;  //Activated
                user.activate_key = "";
                //user.activate_until = DateTime.Now;
            }
            else
            {
                return BadRequest("Invalid Activate Key!");
            }

            db.Entry(user).State = EntityState.Modified;

            try
            {
                db.SaveChanges();
            }

            catch (DbUpdateConcurrencyException)
            {
                if (!UserModelExists(user.UserId))
                {
                    return NotFound();
                }
                else
                {
                    throw;
                }
            }
            catch (DbUpdateException)
            {
                return NotFound();
            }

            return Ok(user);
        }

        // Get Contact List: api/Users/{id}/contacts
        [HttpGet]
        [Route("api/users/{id}/contacts/{name?}")]
        public IHttpActionResult get_contact_list(int id, String name = "@ALL@")
        {
            UserModel user = db.UserModels.FirstOrDefault(u => u.UserId == id);

            if(user == null)
            {
                return BadRequest("User doesnot exist");
            }

            Dictionary<string, IEnumerable<ContactModel>> dictionary = new Dictionary<string, IEnumerable<ContactModel>>();
            if (name.Equals("@ALL@"))
            {
                dictionary.Add("contacts", user.ContactList);

            }
            else
            {
                dictionary.Add("contacts", user.ContactList.Where(c => c.Name.Contains(name)));
            }
            return Ok(dictionary);
        }

        // Add Contact to List: api/Users/{id}/contacts
        [HttpPost]
        [Route("api/users/{id}/contacts")]
        public IHttpActionResult add_contact(int id,ContactModel contact)
        {
            UserModel user = db.UserModels.FirstOrDefault(u => u.UserId == id);

            UserModel otherUser = db.UserModels.FirstOrDefault(u => u.UserEmail.Equals(contact.Email));
            if (otherUser == null)
            {
                return BadRequest("User doesnot Exist!");
            }

            ContactModel other = user.ContactList.FirstOrDefault(c => c.Email == contact.Email);
            if (other != null)
            {
                return BadRequest("Contact alreay Exist!");
            }

            user.ContactList.Add(contact);

            db.SaveChanges();
            return Ok(contact);
        }

        // Delete Contact to List: api/Users/{id}/contacts/{ContactId}
        [HttpDelete]
        [Route("api/users/{id}/contacts/{contactId}")]
        public IHttpActionResult delete_contact(int id, int contactId)
        {
            UserModel user = db.UserModels.FirstOrDefault(u => u.UserId == id);

            ContactModel contact = user.ContactList.FirstOrDefault(c => c.ContactId == contactId);

            FavoriteModel favorite = user.FavoriteList.FirstOrDefault(f => f.Email.Equals(contact.Email));
            user.FavoriteList.Remove(favorite);

            user.ContactList.Remove(contact);
            db.SaveChanges();
            return Ok(contact);
        }

        // Update Contact to List: api/Users/{id}/contacts/{ContactId}
        [HttpPut]
        [Route("api/users/{id}/contacts/{contactId}")]
        public IHttpActionResult update_contact(int id, int contactId,  ContactModel contact)
        {
            UserModel user = db.UserModels.FirstOrDefault(u => u.UserId == id);

            ContactModel other = user.ContactList.FirstOrDefault(c => c.Email.Equals(contact.Email));
            if (other == null)
            {
                return BadRequest("This Contact is not exist!");
            }
            other.Name = contact.Name;
            other.Email = contact.Email;
            db.SaveChanges();
            return Ok(other);
        }

        // Get Favorist List: api/Users/{id}/favors
        [HttpGet]
        [Route("api/users/{id}/favorites")]
        public IHttpActionResult get_favorite_list(int id)
        {
            UserModel user = db.UserModels.FirstOrDefault(u => u.UserId == id);
            string name = "@ALL@";
            Dictionary<string, IEnumerable<FavoriteModel>> dictionary = new Dictionary<string, IEnumerable<FavoriteModel>>();
            if (name.Equals("@ALL@"))
            {
                dictionary.Add("favors", user.FavoriteList);
            }
            else
            {
                dictionary.Add("favors", user.FavoriteList.Where(f => f.Name.Contains(name)));
            }
            return Ok(dictionary);
        }

        // Add Favor user to List: api/Users/{id}/favorite
        [HttpPost]
        [Route("api/users/{id}/favorites")]
        public IHttpActionResult add_favorite(int id, FavoriteModel favorite)
        {
            UserModel user = db.UserModels.FirstOrDefault(u => u.UserId == id);

            UserModel otherUser = db.UserModels.FirstOrDefault(u => u.UserEmail == favorite.Email);
            if (otherUser == null)
            {
                return BadRequest("User doesnot Exist!");
            }

            FavoriteModel other = user.FavoriteList.FirstOrDefault(f => f.Email == favorite.Email);
            if (other != null)
            {
                return BadRequest("Favorite alreay Exist!");
            }

            user.FavoriteList.Add(favorite);

            db.SaveChanges();
            return Ok(favorite);
        }

        // Delete Favorist to List: api/Users/{id}/favorite/{FavorId}
        [HttpDelete]
        [Route("api/users/{id}/favorites/{FavorId}")]
        public IHttpActionResult delete_favorite(int id, int FavorId)
        {
            UserModel user = db.UserModels.FirstOrDefault(u => u.UserId == id);

            FavoriteModel favor = user.FavoriteList.FirstOrDefault(f => f.FavorId == FavorId);
            user.FavoriteList.Remove(favor);
            db.SaveChanges();
            return Ok(favor);
        }

        // Update Contact to List: api/Users/{id}/Favorite/{FavorId}
        [HttpPut]
        [Route("api/users/{id}/favorites/{FavorId}")]
        public IHttpActionResult update_favor(int id, int FavorId, FavoriteModel Favorite)
        {
            UserModel user = db.UserModels.FirstOrDefault(u => u.UserId == id);

            FavoriteModel other = user.FavoriteList.FirstOrDefault(f => f.FavorId == FavorId);
            if (other == null)
            {
                return BadRequest("This Contact is not exist!");
            }
            other.Name = Favorite.Name;
            other.Email = Favorite.Email;
            db.SaveChanges();
            return Ok(user);
        }

        // Create Call 
        [HttpPost]
        [Route("api/users/{id}/call/{ContactId}")]
        public IHttpActionResult create_room(int id, int ContactId)
        {
            UserModel caller = db.UserModels.FirstOrDefault(u => u.UserId == id);

            ContactModel contact = caller.ContactList.FirstOrDefault(c => c.ContactId == ContactId);
            if (contact == null)
            {
                return BadRequest("This Contact is not exist!");
            }
            UserModel callee = db.UserModels.FirstOrDefault(u => u.UserEmail == contact.Email);

            return Ok();
        }

        private string md5(string data)
        {
            using (System.Security.Cryptography.MD5 md5 = System.Security.Cryptography.MD5.Create())
            {
                byte[] inputBytes = System.Text.Encoding.ASCII.GetBytes(data);
                byte[] hashBytes = md5.ComputeHash(inputBytes);

                // Convert the byte array to hexadecimal string
                StringBuilder sb = new StringBuilder();
                for (int i = 0; i < hashBytes.Length; i++)
                {
                    sb.Append(hashBytes[i].ToString("X2"));
                }
                return sb.ToString();
            }
        }


        // Get Contact List: api/Users/{id}/history
        [HttpGet]
        [Route("api/users/{id}/history")]
        public IHttpActionResult get_history_list(int id)
        {
            UserModel user = db.UserModels.FirstOrDefault(u => u.UserId == id);

            if (user == null)
            {
                return BadRequest("User doesnot exist");
            }

            Dictionary<string, IEnumerable<HistoryModel>> dictionary = new Dictionary<string, IEnumerable<HistoryModel>>();
            dictionary.Add("history", user.HistoryList);

            return Ok(dictionary);
        }

        // Add Contact to List: api/Users/{id}/history
        [HttpPost]
        [Route("api/users/{id}/history")]
        public IHttpActionResult add_history(int id, HistoryModel history)
        {
            UserModel user = db.UserModels.FirstOrDefault(u => u.UserId == id);

            //UserModel otherUser = db.UserModels.FirstOrDefault(u => u.UserEmail.Equals(history.CallerEmail));
            //if (otherUser == null)
            //{
            //    return BadRequest("User doesnot Exist!");
            //}


            user.HistoryList.Add(history);

            db.SaveChanges();
            return Ok(history);
        }

        // Delete Contact to List: api/Users/{id}/history/{historyId}
        [HttpDelete]
        [Route("api/users/{id}/history/{historyId}")]
        public IHttpActionResult delete_history(int id, int historyId)
        {
            UserModel user = db.UserModels.FirstOrDefault(u => u.UserId == id);
            HistoryModel history = user.HistoryList.FirstOrDefault(h => h.HistoryId == historyId);
            user.HistoryList.Remove(history);
            db.SaveChanges();
            return Ok(history);
        }

        // Update Contact to List: api/Users/{id}/contacts/{ContactId}
        [HttpPut]
        [Route("api/users/{id}/history/{historyId}")]
        public IHttpActionResult update_history(int id, int historyId, HistoryModel history)
        {
            UserModel user = db.UserModels.FirstOrDefault(u => u.UserId == id);

            HistoryModel other = user.HistoryList.FirstOrDefault(h => h.HistoryId == historyId);
            if (other == null)
            {
                return BadRequest("This Contact is not exist!");
            }
            other.CallSummary = history.CallSummary;
            other.CallDate = history.CallDate;
            db.SaveChanges();
            return Ok(other);
        }



        protected override void Dispose(bool disposing)
        {
            if (disposing)
            {
                db.Dispose();
            }
            base.Dispose(disposing);
        }

        private bool UserModelExists(int id)
        {
            return db.UserModels.Count(e => e.UserId == id) > 0;
        }
    }
}