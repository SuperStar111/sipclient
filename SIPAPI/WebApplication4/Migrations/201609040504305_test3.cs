namespace WebApplication4.Migrations
{
    using System;
    using System.Data.Entity.Migrations;
    
    public partial class test3 : DbMigration
    {
        public override void Up()
        {
            AlterColumn("dbo.HistoryModels", "CallDate", c => c.String());
        }
        
        public override void Down()
        {
            AlterColumn("dbo.HistoryModels", "CallDate", c => c.DateTime(nullable: false));
        }
    }
}
