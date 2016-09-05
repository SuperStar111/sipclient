namespace WebApplication4.Migrations
{
    using System;
    using System.Data.Entity.Migrations;
    
    public partial class test : DbMigration
    {
        public override void Up()
        {
            CreateTable(
                "dbo.HistoryModels",
                c => new
                    {
                        HistoryId = c.Int(nullable: false, identity: true),
                        CalleeEmail = c.String(),
                        CallSummary = c.String(),
                        CallDate = c.DateTime(nullable: false),
                        UserModel_UserId = c.Int(),
                    })
                .PrimaryKey(t => t.HistoryId)
                .ForeignKey("dbo.UserModels", t => t.UserModel_UserId)
                .Index(t => t.UserModel_UserId);
            
        }
        
        public override void Down()
        {
            DropForeignKey("dbo.HistoryModels", "UserModel_UserId", "dbo.UserModels");
            DropIndex("dbo.HistoryModels", new[] { "UserModel_UserId" });
            DropTable("dbo.HistoryModels");
        }
    }
}
