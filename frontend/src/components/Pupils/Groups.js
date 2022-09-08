import React from "react";
import HtmlForm from "../Forms/HtmlForm";
import {useForm} from "react-hook-form";
import Loading from "../Loading";
import {Alert} from "react-bootstrap";
import {prepareCheckboxGroup} from "../../utils";
import useFetchList from "../../hooks/useFetchList";

export default function Groups({ pupilGroups, onSubmit }) {
    const [checkboxes, error, loading] = useFetchList({
        fetchUrl: '/groups/',
        propertyName: 'groupList',
        mapCallback: prepareCheckboxGroup('id', 'name' ),
    });

    let methods = useForm({
        defaultValues: {
            groups: pupilGroups
        }
    });

    const fields = [{
        id: 'groups',
        type: 'checkbox',
        options: checkboxes,
    }];

    return (
        <>
            <Loading show={loading} />
            {!loading && error && <Alert variant="danger">{error}</Alert> }
            {!loading && !error && (
                <>
                    <h3>Groups</h3>
                    <HtmlForm
                        formProps={methods}
                        fields={fields}
                        submitCallback={onSubmit}
                        submitLabel={"Update Groups"}
                        submitClass="w-100"
                    />
                </>
            )}
        </>
    );
}