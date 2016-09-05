namespace WebApplication4.Migrations
{
    using System;
    using System.Data.Entity.Migrations;
    
    public partial class tst : DbMigration
    {
        public override void Up()
        {
            AddColumn("dbo.HistoryModels", "CallerEmail", c => c.String());
        }
        
        public override void Down()
        {
            DropColumn("dbo.HistoryModels", "CallerEmail");
        }
    }
}
