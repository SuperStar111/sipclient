namespace WebApplication4.Migrations
{
    using System;
    using System.Data.Entity.Migrations;
    
    public partial class InitialCreate : DbMigration
    {
        public override void Up()
        {
            CreateTable(
                "dbo.UserModels",
                c => new
                    {
                        UserId = c.Int(nullable: false, identity: true),
                        UserName = c.String(nullable: false),
                        UserEmail = c.String(nullable: false),
                        Password = c.String(nullable: false),
                        activate_key = c.String(),
                        activate_until = c.DateTime(nullable: false),
                        activate_flag = c.Int(nullable: false),
                        ConnectionId = c.Int(nullable: false),
                    })
                .PrimaryKey(t => t.UserId);
            
            CreateTable(
                "dbo.ContactModels",
                c => new
                    {
                        ContactId = c.Int(nullable: false, identity: true),
                        Name = c.String(),
                        Email = c.String(),
                        UserModel_UserId = c.Int(),
                    })
                .PrimaryKey(t => t.ContactId)
                .ForeignKey("dbo.UserModels", t => t.UserModel_UserId)
                .Index(t => t.UserModel_UserId);
            
            CreateTable(
                "dbo.FavoriteModels",
                c => new
                    {
                        FavorId = c.Int(nullable: false, identity: true),
                        Name = c.String(),
                        Email = c.String(),
                        UserModel_UserId = c.Int(),
                    })
                .PrimaryKey(t => t.FavorId)
                .ForeignKey("dbo.UserModels", t => t.UserModel_UserId)
                .Index(t => t.UserModel_UserId);
            
        }
        
        public override void Down()
        {
            DropForeignKey("dbo.FavoriteModels", "UserModel_UserId", "dbo.UserModels");
            DropForeignKey("dbo.ContactModels", "UserModel_UserId", "dbo.UserModels");
            DropIndex("dbo.FavoriteModels", new[] { "UserModel_UserId" });
            DropIndex("dbo.ContactModels", new[] { "UserModel_UserId" });
            DropTable("dbo.FavoriteModels");
            DropTable("dbo.ContactModels");
            DropTable("dbo.UserModels");
        }
    }
}
