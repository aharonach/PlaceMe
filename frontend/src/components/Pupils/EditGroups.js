import React from 'react';
import {Alert, Col, Row} from "react-bootstrap";
import Loading from "../Loading";
import Groups from "./Groups";
import GroupsAttributes from "./GroupsAttributes";
import {useOutletContext} from "react-router-dom";
import useFetchList from "../../hooks/useFetchList";

export default function EditGroups() {
    const { pupil } = useOutletContext();

    const [pupilGroups, loading, error, axiosFetch] = useFetchList({
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
            {!loading && error && <Alert variant="danger">{error}</Alert>}
            {!loading && !error && pupilGroups && (
                <Row>
                    <Col md={4}>
                        <div className="p-3 bg-light rounded-3">
                            <Groups pupilGroups={pupilGroups.map(group => group.id.toString())} onSubmit={updateGroups} />
                        </div>
                    </Col>
                    <Col md={8}>
                        <GroupsAttributes groups={pupilGroups} />
                    </Col>
                </Row>
            )}
        </>
    );
}