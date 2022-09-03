import {Route, Routes} from "react-router-dom";
import {Page} from "./layouts";
import {HomePage} from "./components/Home";
import * as Pupil from "./components/Pupils";
import * as Group from "./components/Groups";
import * as Template from "./components/Templates";
import * as Placement from "./components/Placements";
import ErrorPage from "./components/ErrorPage";
import React from "react";

export default function AppRoutes() {
    return (
        <Routes>
            <Route path="/" element={<Page><HomePage /></Page>} />
            <Route path="/pupils" element={<Page />}>
                <Route index element={<Pupil.List />} />
                <Route path="add" element={<Pupil.Add />} />
                <Route path=":pupilId" element={<Pupil.Page />}>
                    <Route index element={<Pupil.Data />} />
                    <Route path="edit" element={<Pupil.Edit />} />
                    <Route path="groups" element={<Pupil.Groups />} />
                </Route>
            </Route>
            <Route path="/groups" element={<Page />}>
                <Route index element={<Group.List />} />
                <Route path="add" element={<Group.Add />} />
                <Route path=":groupId" element={<Group.Page />}>
                    <Route index element={<Group.Data />} />
                    <Route path="edit" element={<Group.Edit />} />
                    <Route path="preferences" element={<Group.Preferences />} />
                </Route>
            </Route>
            <Route path="/templates" element={<Page />}>
                <Route index element={<Template.List />} />
                <Route path="add" element={<Template.Add />} />
                <Route path=":templateId" element={<Template.Page />}>
                    <Route index element={<Template.Data />} />
                    <Route path="edit" element={<Template.Edit />} />
                </Route>
            </Route>
            <Route path="/placements" element={<Page />}>
                <Route index element={<Placement.List />} />
                <Route path="add" element={<Placement.Add />} />
                <Route path=":placementId" element={<Placement.Page />}>
                    <Route index element={<Placement.Data />} />
                    <Route path="edit" element={<Placement.Edit />} />
                    <Route path="results">
                        <Route index element={<Placement.Result.List />} />
                        <Route path="add" element={<Placement.Result.Add />} />
                        <Route path=":resultId" element={<Placement.Result.Page />}>
                            <Route index element={<Placement.Result.Data />} />
                            <Route path="edit" element={<Placement.Result.Edit />} />
                        </Route>
                    </Route>
                </Route>
            </Route>
            <Route path="*" element={<ErrorPage />}></Route>
        </Routes>
    );
}