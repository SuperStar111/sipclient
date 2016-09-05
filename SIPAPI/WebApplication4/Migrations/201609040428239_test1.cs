namespace WebApplication4.Migrations
{
    using System;
    using System.Data.Entity.Migrations;
    
    public partial class test1 : DbMigration
    {
        public override void Up()
        {
            AddColumn("dbo.HistoryModels", "callStatus", c => c.Int(nullable: false));
            DropColumn("dbo.HistoryModels", "CalleeEmail");
        }
        
        public override void Down()
        {
            AddColumn("dbo.HistoryModels", "CalleeEmail", c => c.String());
            DropColumn("dbo.HistoryModels", "callStatus");
        }
    }
}
