import React, {useState} from 'react';
import {Alert} from "react-bootstrap";
import Loading from "../Loading";
import Groups from "./Groups";
import GroupsAttributes from "./GroupsAttributes";
import {useOutletContext} from "react-router-dom";
import useFetchList from "../../hooks/useFetchList";

export default function EditGroups() {
    const { pupil } = useOutletContext();

    const [pupilGroups, errorPupilGroups, loadingPupilGroups, axiosFetch] = useFetchList({
        fetchUrl: `/pupils/${pupil.id}/groups`,
        propertyName: "groupList",
    });

    // const [groups, errorGroups, loadingGroups] = useFetchList({
    //     fetchUrl: '/groups',
    //     propertyName: "groupList",
    // });

    const loading = loadingPupilGroups /*|| loadingGroups*/;
    const error = errorPupilGroups /*|| errorGroups*/;

    const updateGroups = data => {
        axiosFetch({
            method: 'post',
            url: `/pupils/${pupil.id}/groups`,
            data: data.groups,
        });
    }

    return (
        <>
            <h2>Edit Groups</h2>
            <Loading show={loading} />
            {!loading && error && <Alert variant="danger">{error}</Alert>}
            {!loading && !error && pupilGroups && (
                <>
                    <Groups pupilGroups={pupilGroups.map(group => group.id.toString())} onSubmit={updateGroups} />
                    <GroupsAttributes groups={pupilGroups} />
                </>
            )}
        </>
    );
}