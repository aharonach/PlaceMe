import React from "react";
import {Route, Routes} from "react-router-dom";
import Header from "./layouts/Header";
import Footer from "./layouts/Footer";
import * as Pages from './pages';
import * as Pupil from "./components/Pupils";
import * as Group from "./components/Groups";
import * as Template from "./components/Templates";
import {Container} from "react-bootstrap";

function App() {
    return (
        <div className="App">
            <Header />
            <Container>
                <Routes>
                    <Route path="/" element={<Pages.Home />}></Route>
                    <Route path="/pupils" element={<Pages.Pupils />}>
                        <Route index element={<Pupil.PupilsList />}></Route>
                        <Route path="add" element={<Pupil.AddPupil />}></Route>
                        <Route path=":pupilId" element={<Pupil.Profile />}></Route>
                    </Route>
                    <Route path="/groups" element={<Pages.Groups />}>
                        <Route index element={<Group.GroupsList />} />
                        <Route path="add" element={<Group.AddGroup />} />
                        <Route path=":groupId" element={<Group.GroupPage />} />
                    </Route>
                    <Route path="/templates" element={<Pages.Templates />}>
                        <Route index element={<Template.TemplatesList />} />
                        <Route path="add" element={<Template.AddTemplate />} />
                        <Route path=":templateId" element={<Template.TemplatePage />} />
                    </Route>
                    <Route path="/placements" element={<Pages.Placements />}></Route>
                    <Route path="*" element={<Pages.ErrorPage />}></Route>
                </Routes>
                <Footer />
            </Container>
        </div>
    );
}

export default App;
