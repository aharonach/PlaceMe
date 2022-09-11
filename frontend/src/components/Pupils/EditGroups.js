import React from 'react';
import {Alert, Col, Row} from "react-bootstrap";
import Loading from "../Loading";
import Groups from "./Groups";
import GroupsAttributes from "./GroupsAttributes";
import {useOutletContext} from "react-router-dom";
import useFetchList from "../../hooks/useFetchList";

export default function EditGroups() {
    const { pupil } = useOutletContext();

    const [pupilGroups, error, loading, axiosFetch] = useFetchList({
        fetchUrl: `/pupils/${pupil.id}/groups`,
        propertyName: "groupList",
    });

    const updateGroups = data => {
        axiosFetch({
            method: 'post',
            url: `/pupils/${pupil.id}/groups`,
            data: data.groups,
        });
    }

    return (
        <>
            <h2>Attribute Values</h2>
            <Loading show={loading} />
            {error && <Alert variant="danger">{error}</Alert>}
            {pupilGroups && (
                <Row>
                    <Col md={4}>
                        <Groups pupilGroups={pupilGroups.map(group => group.id.toString())} onSubmit={updateGroups} />
                    </Col>
                    <Col md={8}>
                        <GroupsAttributes groups={pupilGroups} />
                    </Col>
                </Row>
            )}
        </>
    );
}