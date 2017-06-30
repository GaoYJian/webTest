using System;
using System.Collections.Generic;
using System.Linq;
using System.Web;
using System.Web.UI;
using System.Web.UI.WebControls;
//using JXSoft.TicketManage;
using System.Text.RegularExpressions;
using System.Data;
using System.Collections;

namespace WebApplication3
{
    public partial class _Default : System.Web.UI.Page
    {
        private static String DESC = " DESC";
        private static String ASC = " ASC";
        private static String sortExpression="Id";
        private static String sortDirection = ASC;
        private static String dept;
        public SortDirection GridViewSortDirection
        {
            get
            {
                if (ViewState["sortDirection"] == null)
                    ViewState["sortDirection"] = SortDirection.Ascending;
                return (SortDirection)ViewState["sortDirection"];
            }
            set { ViewState["sortDirection"] = value; }
        }



        private static String[] attrs = new String[18] {"Id", "Name",
        "CaseNo", "BedCode","Dept","CurVisitStatus",
        "Ward","Sex", "Age","BedId","AdmitDit","DischargeDt",
        "CurNurseLevel","CostTotal","DepositTotal","LeaveDepositTotal",
        "InWstDiagnose", "DeptId"
        };
        private ArrayList fillValues(Hospital.VisitEntity person)
        {
            ArrayList values = new ArrayList();
            values.Add(person.Id);
            values.Add(person.Name);
            values.Add(person.CaseNo);
            values.Add(person.BedCode);
            values.Add(person.Dept.TrimEnd());
            values.Add(person.CurVisitStatus);
            values.Add(person.Ward);
            values.Add(person.Sex);
            string age = person.Age;
            if (person.Age[0] == 'Y')
                values.Add(int.Parse(age.Substring(1, age.Length - 1)));
            else if (age.Trim().Length != 0)
                values.Add(int.Parse(age));
            else values.Add(-1);
            values.Add(person.BedId);
            values.Add(person.AdmitDt);
            values.Add(person.DischargeDt);
            values.Add(person.CurNurseLevel);
            values.Add(person.CostTotal);
            values.Add(person.DepositTotal);
            values.Add(person.LeaveDepositTotal);
            values.Add(person.InWstDiagnose);
            values.Add(person.DeptId);
            return values;
        }
        protected void Page_Load(object sender, EventArgs e)
        {
            if (!IsPostBack)
            {
                Hospital.Service1SoapClient ss = new Hospital.Service1SoapClient();
                string s = ss.GetSQLConnect();
                string q = Request.Url.Query;
                int index = q.IndexOf("index=");
                string user=q.Substring(6,index-6);
                Hospital.DeptEntity[] get = ss.GetUserDept(user);
                int i = int.Parse(q.Substring(index + 6));
                dept = get[i].Name;
                input.Value = dept;
            }
            BindData();

        }
       

        protected void BindData()
        {
            selapply.Visible = true;
            Hospital.Service1SoapClient ss = new Hospital.Service1SoapClient();
            string s = ss.GetSQLConnect();
            Hospital.VisitEntity[] get = ss.GetVisitData(dept);
            GridView1.PageSize = int.Parse(pageSize.SelectedItem.Value.ToString());
            DataTable dt = new DataTable();
            if (get.Length == 0) return;
            ArrayList all = fillValues(get[0]);
            for (int i = 0; i < attrs.Length; i++)
            {
                if (attrs[i].Equals("Age") || attrs[i].Equals("BedCode") || attrs[i].Equals("BedId") ||
                    attrs[i].Equals("CaseNo") || all[i].GetType() == typeof(int))
                {
                    DataColumn agecol = new DataColumn(attrs[i]);
                    agecol.DataType = typeof(int);
                    dt.Columns.Add(agecol);
                    continue;
                }
                else if(attrs[i].Equals("AdmitDit")||attrs[i].Equals("DischargeDt")){
                    DataColumn datecol = new DataColumn(attrs[i]);
                    datecol.DataType=typeof(DateTime);
                    dt.Columns.Add(datecol);
                    continue;
                }
                dt.Columns.Add(attrs[i]);
                
            }
            for(int i = 0; i < get.Length; i++){
                ArrayList al = fillValues(get[i]);
                DataRow dr = dt.NewRow();
                for (int j = 0; j < attrs.Length; j++)
                {

                    dr[attrs[j]] = al[j];
                        
                }
                dt.Rows.Add(dr);
            
            }
            Session["hi"] = dt;


            DataRow[] sel_drs = dt.Select(seletion());
            DataTable sel_tbl = new DataTable();
            sel_tbl = dt.Clone();
            foreach (DataRow dr in sel_drs)
            {
                sel_tbl.Rows.Add(dr.ItemArray);
            }
            DataView dv = new DataView(sel_tbl);
            dv.Sort = sortExpression + sortDirection;
            if (ward.Items.Count == 1)
            {
                DataTable tmp = dv.ToTable(true, "Dept");
                foreach (DataRow a in tmp.Columns["Dept"].Table.Rows)
                {
                    ListItem item = new ListItem(a.ItemArray[0].ToString(), a.ItemArray[0].ToString());
                    ward.Items.Add(item);
                }
            }


            GridView1.DataSource = dv;
            GridView1.DataBind();
        }


        protected void GridView1_PageIndexChanging(object sender, GridViewPageEventArgs e)
        {
            GridView gvw = (GridView)sender;
            if (e.NewPageIndex < 0)
            {
                TextBox pageNum = (TextBox)gvw.BottomPagerRow.FindControl("txtNewPageIndex");
                int Pa = int.Parse(pageNum.Text);
                if (Pa <= 0)
                {
                    gvw.PageIndex = 0;
                }
                else
                {
                    gvw.PageIndex = Pa - 1;
                }
            }
            else
            GridView1.PageIndex = e.NewPageIndex;
            BindData();
        }

        protected void pageSize_changed(object sender, EventArgs e)
        {

            BindData();
        }
        protected void GridView1_Sorting(object sender, GridViewSortEventArgs e)
        {
            sortExpression = e.SortExpression;

            if (GridViewSortDirection == SortDirection.Ascending)
            {
                GridViewSortDirection = SortDirection.Descending;
                sortDirection = DESC;
                BindData();
            }
            else
            {
                GridViewSortDirection = SortDirection.Ascending;
                sortDirection = ASC;
                
                BindData();
            }

        }
        private String seletion()
        {
            string res = "Id>'0'";
            if(sex_sl.SelectedValue != "all")
                res += " and Sex='" + sex_sl.SelectedValue + "'";
            if (curvisitstatus_sl.SelectedValue != "all")
                res += " and CurVisitStatus='" + curvisitstatus_sl.SelectedValue + "'";
            if (minage_sl.Value != "任意"&&minage_sl.Value.Length!=0)
            {
                try
                {
                    int a = int.Parse(minage_sl.Value);
                    wrong.Visible = false;
                }
                catch (Exception e)
                {
                    wrong.Visible = true;
                    return "";
                }
                res += " and Age>='" + minage_sl.Value + "'";
            }
            if (maxage_sl.Value != "任意" && maxage_sl.Value.Length != 0)
            {
                try
                {
                    int a = int.Parse(minage_sl.Value);
                    wrong.Visible = false;
                }
                catch (Exception e)
                {
                    wrong.Visible = true;
                    return "";
                }
                res += " and Age<='" + maxage_sl.Value + "'";
            }
            if (minid_sl.Value != "任意" && maxage_sl.Value.Length != 0)
            {
                try
                {
                    int a = int.Parse(minage_sl.Value);
                    wrong.Visible = false;
                }
                catch (Exception e)
                {
                    wrong.Visible = true;
                    return "";
                }
                res += " and Id>='" + maxage_sl.Value + "'";
            }
            if (maxid_sl.Value != "任意" && maxage_sl.Value.Length != 0)
            {
                try
                {
                    int a = int.Parse(minage_sl.Value);
                    wrong.Visible = false;
                }
                catch (Exception e)
                {
                    wrong.Visible = true;
                    return "";
                }
                res += " and Id<='" + maxage_sl.Value + "'";
            }
            if (in_hos.Checked)
            {
                res += "and DischargeDt='0001/1/1 0:00:00'";
              
            }
            if (earliest_in_sl.Value.Length != 0 && earliest_in_sl.Value[0] != 'y')
            {
                DateTime dt = DateTime.Parse(earliest_in_sl.Value);
                res += " and AdmitDit>='" + dt.ToString() + "'";
            }
            if (latest_in_sl.Value.Length != 0 && latest_in_sl.Value[0] != 'y')
            {
                DateTime dt = DateTime.Parse(latest_in_sl.Value);
                res += " and DischargeDt<='" + dt.ToString() + "'";
            }
            if (ward.SelectedValue != "all")
            {
                res += " and Dept='" + ward.SelectedValue + "'";
            }
            return res;
        }
        protected void selapply_click(object sender, EventArgs e)
        {
            selectform(seletion());
        }
        private void selectform(String condition)
        {
            DataTable dt = (DataTable) Session["hi"];
            DataRow[] sel_drs = dt.Select(condition);
            DataTable sel_tbl = new DataTable();
            sel_tbl = dt.Clone();
            foreach (DataRow dr in sel_drs)
            {
                sel_tbl.Rows.Add(dr.ItemArray);
            }
            DataView dv = new DataView(sel_tbl);
            dv.Sort = sortExpression + sortDirection;

            GridView1.DataSource = dv;
            GridView1.DataBind();
        }
        protected void logout_click(object sender, EventArgs e)
        {
            Response.Redirect("http://localhost:15686/WebForm1.aspx");
            Session["dept"] = null;
            Session["hi"] = null;
        }

    }
}