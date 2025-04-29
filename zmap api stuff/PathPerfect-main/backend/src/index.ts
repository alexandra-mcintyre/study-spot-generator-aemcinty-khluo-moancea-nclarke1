import express from "express";
import {Db, MongoClient} from "mongodb";
import bodyParser from "body-parser";
import cors from "cors";

const app = express();
const port = 8080; // default port to listen
let db: Db;

app.use(express.json());
app.use(cors({
  origin: 'http://localhost:3000'
}))

app.use(bodyParser.urlencoded({extended: false}));

app.get("/dist", async (req, res) => {
    const collection = db.collection("dist");
    const result = await collection.find({}).toArray()
    return res.json(result);
});

app.post("/dist", async (req, res) => {
    const postBodyData = req.body;
    const collection = db.collection("dist");
    const newPost = {dist: postBodyData.distance};
    console.log (newPost);
    try {
        await collection.insertOne(newPost);
        return res.json(newPost);
    } catch (e) {
        return res.status(500).send();
    }
});

// start the Express server
function start() {
    const client = new MongoClient("mongodb+srv://echo:newpassword@cluster0.ao9hrh9.mongodb.net/?retryWrites=true&w=majority");
    client.connect()
        .then(() => {
            console.log('Connected successfully to server');
            db = client.db("database");
            app.listen(port, () => {
                console.log(`server started at http://localhost:${port}`);
            });
        })
        .catch(() => {
            console.log("error connecting to mongoDB!");
        });
}

start();