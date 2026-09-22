const express=require('express');
require('dotenv').config();
const app=express();
const PORT=process.env.PORT||8080;
app.get('/api/train/:number/live',async(req,res)=>{
 const n=String(req.params.number||'').replace(/\D/g,'');
 if(!n)return res.status(400).json({success:false,error:'Invalid train number'});
 try{
  const r=await fetch(`https://api.railradar.in/v1/trains/${n}/live`,{headers:{Authorization:`Bearer ${process.env.RAILRADAR_API_KEY}`}});
  const body=await r.text(); res.status(r.status).type('application/json').send(body);
 }catch(e){res.status(502).json({success:false,error:'Live provider unavailable'});}
});
app.listen(PORT,()=>console.log(`RailTrack backend listening on ${PORT}`));
