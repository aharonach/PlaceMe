import React from "react";
import {Button, Spinner} from "react-bootstrap";
import useAxios from "../../hooks/useAxios";
import Api from "../../api";

export default function DeletePupilFromGroup({ pupilId, groupId, updated, setUpdated, children }) {
    // eslint-disable-next-line no-unused-vars
    const [response, error, loading, axiosFetch] = useAxios();

    const handleDelete = () => {
        axiosFetch({
            axiosInstance: Api,
            method: 'delete',
            url: `/pupils/${pupilId}/groups`,
            data: groupId
        }).then(() => setUpdated && setUpdated(!updated));
    };

    return (
        <Button variant="danger" size="sm" onClick={() => handleDelete()}>
            {loading && <Spinner
                as="span"
                animation="border"
                role="status"
                size="sm"
                aria-hidden="true"
            />}
            {children}
        </Button>
    )
}