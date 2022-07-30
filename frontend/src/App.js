import React from "react";
import {Route, Routes} from "react-router-dom";
import Header from "./layouts/Header";
import Footer from "./layouts/Footer";
import * as Pages from './pages';
import * as Pupil from "./components/Pupils";

function App() {
    return (
        <div className="App">
            <Header />
            <Routes>
                <Route path="/" element={<Pages.Home />}></Route>
                <Route path="/pupils" element={<Pages.Pupils />}>
                    <Route index element={<Pupil.PupilsList />}></Route>
                    <Route path="add" element={<Pupil.AddPupil />}></Route>
                    <Route path=":pupilId" element={<Pupil.Profile />}></Route>
                </Route>
                <Route path="/groups" element={<Pages.Groups />}></Route>
                <Route path="/templates" element={<Pages.Templates />}></Route>
                <Route path="/attributes" element={<Pages.Attributes />}></Route>
                <Route path="/placements" element={<Pages.Placements />}></Route>
                <Route path="*" element={<Pages.ErrorPage />}></Route>
            </Routes>
            <Footer />
        </div>
    );
}

export default App;
