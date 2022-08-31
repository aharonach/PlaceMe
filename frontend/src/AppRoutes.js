import {Route, Routes} from "react-router-dom";
import {Page} from "./layouts";
import {HomePage} from "./components/Home";
import * as Pupil from "./components/Pupils";
import * as Group from "./components/Groups";
import * as Template from "./components/Templates";
import * as Placement from "./components/Placements";
import ErrorPage from "./components/ErrorPage";
import React from "react";
import RecordDetails from "./components/RecordDetails";

export default function AppRoutes() {
    return (
        <Routes>
            <Route path="/" element={<Page><HomePage /></Page>}></Route>
            <Route path="/pupils" element={<Page />}>
                <Route index element={<Pupil.PupilsList />}></Route>
                <Route path="add" element={<Pupil.AddPupil />}></Route>
                <Route path=":pupilId" element={<Pupil.Profile />}>
                    <Route index element={<Pupil.PupilData />}></Route>
                    <Route path="edit" element={<Pupil.EditPupil />}></Route>
                    <Route path="groups" element={<Pupil.Groups />}></Route>
                </Route>
            </Route>
            <Route path="/groups" element={<Page />}>
                <Route index element={<Group.GroupsList />} />
                <Route path="add" element={<Group.AddGroup />} />
                <Route path=":groupId" element={<Group.GroupPage />}>
                    <Route index element={<Group.GroupData />} />
                    <Route path="edit" element={<Group.EditGroup />} />
                    <Route path="preferences" element={<Group.Preferences />} />
                </Route>
            </Route>
            <Route path="/templates" element={<Page />}>
                <Route index element={<Template.TemplatesList />} />
                <Route path="add" element={<Template.AddTemplate />} />
                <Route path=":templateId" element={<Template.TemplatePage />} />
            </Route>
            <Route path="/placements" element={<Page />}>
                <Route index element={<Placement.PlacementsList />}></Route>
                <Route path="add" element={<Placement.AddPlacement />}></Route>
                <Route path=":placementId" element={<Placement.PlacementPage />}></Route>
                <Route path=":placementId/edit" element={<Placement.PlacementPage edit={true} />}></Route>
                <Route path=":placementId/results" element={<Placement.PlacementResultsList />}></Route>
                <Route path=":placementId/results/:resultId" element={<Placement.PlacementResultPage />}></Route>
            </Route>
            <Route path="*" element={<ErrorPage />}></Route>
        </Routes>
    );
}